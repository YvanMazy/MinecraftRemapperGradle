package be.yvanmazy.minecraftremapper.gradle.remap;

import be.yvanmazy.minecraftremapper.gradle.MinecraftRemapperGradle;
import be.yvanmazy.minecraftremapper.gradle.data.PreparedData;
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.repo.ClassRepo;
import net.md_5.specialsource.repo.JarRepo;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Objects;

public class RemappingManager {

    private final MinecraftRemapperGradle plugin;

    private String currentVersion;
    private ClassRepo classRepo;
    private JarRemapper remapper;

    public RemappingManager(final @NotNull MinecraftRemapperGradle plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
    }

    public byte @NotNull [] remap(final byte @NotNull [] bytes) {
        if (this.classRepo == null || this.remapper == null || !this.plugin.getExtension().getVersion().equals(this.currentVersion)) {
            this.prepareRemapper();
        }
        return this.remapper.remapClassFile(bytes, this.classRepo);
    }

    private void prepareRemapper() {
        this.currentVersion = this.plugin.getExtension().getVersion();
        final PreparedData preparedData = this.plugin.getDataManager().fetchData();

        try {
            this.classRepo = new JarRepo(Jar.init(preparedData.versionJarPath().toFile()));
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to load version jar", e);
        }

        final JarMapping mapping = new JarMapping();
        try {
            try (final BufferedReader reader = Files.newBufferedReader(preparedData.mappingPath())) {
                mapping.loadMappings(reader, null, null, true);
            }
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to load mappings", e);
        } finally {
            this.remapper = new JarRemapper(mapping);
        }
    }

}