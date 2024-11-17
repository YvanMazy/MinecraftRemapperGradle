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

package be.yvanmazy.minecraftremapper.gradle.action;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class RemapAction implements Action<Task> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemapAction.class);

    private final DirectoryProperty directoryProperty;
    private final UnaryOperator<byte[]> remapper;

    public RemapAction(final @NotNull DirectoryProperty directoryProperty, final @NotNull UnaryOperator<byte[]> remapper) {
        this.directoryProperty = Objects.requireNonNull(directoryProperty, "directoryProperty must not be null");
        this.remapper = Objects.requireNonNull(remapper, "remapper must not be null");
    }

    @Override
    public void execute(final @NotNull Task task) {
        for (final File file : this.directoryProperty.getAsFileTree()) {
            final Path path = file.toPath();
            try {
                final byte[] result = this.remapper.apply(Files.readAllBytes(path));
                if (result == null) {
                    continue;
                }
                Files.write(path, result);
            } catch (final IOException e) {
                LOGGER.error("Failed to remap class file '{}'", path, e);
            }
        }
    }

}