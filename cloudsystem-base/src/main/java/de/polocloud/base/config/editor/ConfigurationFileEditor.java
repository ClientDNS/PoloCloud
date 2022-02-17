package de.polocloud.base.config.editor;

import com.google.common.io.Files;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public final class ConfigurationFileEditor {

    @SneakyThrows
    public ConfigurationFileEditor(final File file, final ConfigReplace configReplace) {
        //noinspection UnstableApiUsage
        final var lines = Files.readLines(file, StandardCharsets.UTF_8);

        try (final var fileWriter = new FileWriter(file)) {
            for (final var line : lines) {
                fileWriter.write(configReplace.replace(line) + "\n");
            }
        }

    }

}
