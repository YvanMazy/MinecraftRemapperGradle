package be.yvanmazy.minecraftremapper.gradle.data;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public record PreparedData(@NotNull Path versionJarPath, @NotNull Path mappingPath, @NotNull Path remappedJarPath) {

}