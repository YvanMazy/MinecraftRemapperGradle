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