package be.yvanmazy.minecraftremapper.gradle;

import be.yvanmazy.minecraftremapper.gradle.action.RemapAction;
import be.yvanmazy.minecraftremapper.gradle.data.DataManager;
import be.yvanmazy.minecraftremapper.gradle.extension.RemapperExtension;
import be.yvanmazy.minecraftremapper.gradle.remap.RemappingManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class MinecraftRemapperGradle implements Plugin<Project> {

    private final DataManager dataManager = new DataManager(this);
    private final RemappingManager remappingManager = new RemappingManager(this);

    private Path homePath;
    private RemapperExtension extension;

    @Override
    public void apply(final @NotNull Project project) {
        this.extension = project.getExtensions().create("minecraftRemapper", RemapperExtension.class);

        project.afterEvaluate(this::initialize);
    }

    private void initialize(final @NotNull Project project) {
        this.extension.validate();
        this.homePath = this.prepareHomePath(project);

        if (this.extension.isIncludeDependency()) {
            final Path jarPath = this.dataManager.fetchData().remappedJarPath();
            if (Files.notExists(jarPath)) {
                throw new IllegalStateException("The remapped jar is not found at '" + jarPath + "'");
            }
            project.getDependencies().add("compileOnly", project.files(jarPath.toFile()));
        }
        if (this.extension.isIncludeLibrariesDependency()) {
            project.getDependencies().add("compileOnly", project.files(this.dataManager.fetchLibraries()));
        }

        if (this.extension.isRemapOnCompile()) {
            project.getTasks().withType(JavaCompile.class, task -> {
                task.doLast(new RemapAction(task.getDestinationDirectory(), this.remappingManager::remap));
                if (this.extension.isCancelCompileCache()) {
                    task.getOutputs().upToDateWhen(t -> false);
                }
            });
        }
    }

    public RemapperExtension getExtension() {
        return this.extension;
    }

    public Path getHomePath() {
        return this.homePath;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public RemappingManager getRemappingManager() {
        return this.remappingManager;
    }

    private @NotNull Path prepareHomePath(final @NotNull Project project) {
        final Path path = Objects.requireNonNullElseGet(this.extension.getHomePath(), () -> getHomePath(project));
        if (!Files.isDirectory(path)) {
            try {
                Files.createDirectories(path);
            } catch (final IOException e) {
                throw new UncheckedIOException("Failed to create plugin directory", e);
            }
        }
        return path;
    }

    private static @NotNull Path getHomePath(final @NotNull Project project) {
        final Path home = project.getGradle().getGradleUserHomeDir().toPath().resolve("caches").resolve("MinecraftRemapperGradle");
        if (Files.isDirectory(home)) {
            try {
                Files.createDirectories(home);
            } catch (final IOException e) {
                throw new UncheckedIOException("Failed to create plugin directory", e);
            }
        }
        return home;
    }

}