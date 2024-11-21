/*
 * MIT License
 *
 * Copyright (c) 2024 Yvan Mazy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
            final var jarDependency = project.files(jarPath);
            this.extension.getDependenciesConfigurations()
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(config -> project.getDependencies().add(config, jarDependency));
        }
        if (this.extension.isIncludeLibrariesDependency()) {
            final var libraryDependency = project.files(this.dataManager.fetchLibraries());
            this.extension.getDependenciesConfigurations()
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(config -> project.getDependencies().add(config, libraryDependency));
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
        final Path path;
        if (this.extension.getHomePath() != null) {
            path = this.extension.getHomePath().toPath();
        } else {
            path = project.getGradle().getGradleUserHomeDir().toPath().resolve("caches").resolve("MinecraftRemapperGradle");
        }
        if (!Files.isDirectory(path)) {
            try {
                Files.createDirectories(path);
            } catch (final IOException e) {
                throw new UncheckedIOException("Failed to create plugin directory", e);
            }
        }
        return path;
    }

}