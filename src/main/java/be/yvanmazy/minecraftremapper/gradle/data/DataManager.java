package be.yvanmazy.minecraftremapper.gradle.data;

import be.yvanmazy.minecraftremapper.DirectionType;
import be.yvanmazy.minecraftremapper.gradle.MinecraftRemapperGradle;
import be.yvanmazy.minecraftremapper.gradle.extension.RemapperExtension;
import be.yvanmazy.minecraftremapper.http.RequestHttpClient;
import be.yvanmazy.minecraftremapper.http.exception.RequestHttpException;
import be.yvanmazy.minecraftremapper.process.RemapperProcessor;
import be.yvanmazy.minecraftremapper.process.exception.ProcessingException;
import be.yvanmazy.minecraftremapper.setting.PreparationSettings;
import be.yvanmazy.minecraftremapper.version.Version;
import be.yvanmazy.minecraftremapper.version.VersionJsonAdapter;
import be.yvanmazy.minecraftremapper.version.fetcher.VersionFetcher;
import be.yvanmazy.minecraftremapper.version.fetcher.exception.VersionFetchingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.gradle.api.GradleScriptException;
import org.gradle.api.InvalidUserDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class DataManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataManager.class);

    private final MinecraftRemapperGradle plugin;

    private final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Version.class, new VersionJsonAdapter()).create();

    private final RequestHttpClient httpClient;
    private final VersionFetcher versionFetcher;
    private Version version;
    private DirectionType directionType;

    private RemapperProcessor processor;

    public DataManager(final MinecraftRemapperGradle plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
        this.httpClient = RequestHttpClient.newDefault();
        this.versionFetcher = VersionFetcher.newMojangFetcher(this.httpClient, this.gson);
    }

    private void initialize() {
        final RemapperExtension extension = this.plugin.getExtension();
        this.directionType = extension.getDirectionType();

        final Path path = this.plugin.getHomePath().resolve("versions.json");
        final List<Version> versions = this.getVersions(path, true);

        final String selectedVersion = extension.getVersion();
        this.version = versions.stream()
                .filter(v -> v.id().equals(selectedVersion))
                .findFirst()
                .or(() -> this.getVersions(path, false).stream().filter(v -> v.id().equals(selectedVersion)).findFirst())
                .orElseThrow(() -> new InvalidUserDataException("Version '" + selectedVersion + "' is not found!"));
    }

    public List<Path> fetchLibraries() {
        final JsonObject json;
        try {
            json = this.gson.fromJson(Files.readString(this.processor.getVersionMetaPath()), JsonObject.class);
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to read version meta", e);
        }
        return json.getAsJsonArray("libraries")
                .asList()
                .stream()
                .map(e -> this.gson.fromJson(e, Library.class))
                .filter(Objects::nonNull)
                .filter(Library::isAllowed)
                .map(this::download)
                .toList();
    }

    public PreparedData fetchData() {
        if (this.processor == null) {
            this.processor = this.process();
        }
        return new PreparedData(this.processor.getVersionJarPath(), this.processor.getMappingPath(), this.processor.getRemappedJarPath());
    }

    private Path download(final Library library) {
        final Library.Downloads.Artifact artifact = library.downloads().artifact();
        final Path path = this.plugin.getHomePath().resolve("libraries").resolve(artifact.path());
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (final IOException e) {
                throw new UncheckedIOException("Failed to create library directory for '" + library.name() + "'", e);
            }
            try {
                // TODO: Check sha1
                Files.write(path, this.httpClient.getBytes(artifact.url()));
            } catch (final RequestHttpException e) {
                LOGGER.error("Failed to download library '{}'", library.name(), e);
            } catch (final IOException e) {
                throw new UncheckedIOException("Failed to write library '" + library.name() + "'", e);
            }
        }
        return path;
    }

    private RemapperProcessor process() {
        if (this.version == null || !this.version.id().equals(this.plugin.getExtension().getVersion())) {
            this.initialize();
        }
        final RemapperProcessor processor = new RemapperProcessor(new PreparationSettings(this.httpClient,
                this.gson,
                this.directionType,
                this.version,
                this.plugin.getHomePath().resolve("data").toString(),
                true,
                false));

        try {
            processor.process();
        } catch (final ProcessingException e) {
            throw new GradleScriptException("Failed to download Minecraft files", e);
        }

        return processor;
    }

    @SuppressWarnings("unchecked")
    private List<Version> getVersions(final Path path, final boolean checkExists) {
        if (checkExists && Files.exists(path)) {
            try (final BufferedReader reader = Files.newBufferedReader(path)) {
                return (List<Version>) this.gson.fromJson(reader,
                        TypeToken.getParameterized(TypeToken.get(List.class).getType(), TypeToken.get(Version.class).getType()));
            } catch (final Exception e) {
                LOGGER.error("Failed to parse versions file", e);
            }
        }
        final List<Version> versions;
        try {
            versions = this.versionFetcher.fetchVersions();
        } catch (final VersionFetchingException e) {
            throw new GradleScriptException("Failed to fetch Minecraft versions", e);
        }
        try {
            Files.writeString(path, this.gson.toJson(versions));
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to save versions", e);
        }
        return versions;
    }

}