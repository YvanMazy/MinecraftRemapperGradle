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

package be.yvanmazy.minecraftremapper.gradle.remap;

import be.yvanmazy.minecraftremapper.gradle.MinecraftRemapperGradle;
import be.yvanmazy.minecraftremapper.gradle.data.PreparedData;
import net.md_5.specialsource.Jar;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.provider.JarProvider;
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
            mapping.setFallbackInheritanceProvider(new JarProvider(Jar.init(preparedData.remappedJarPath().toFile())));
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