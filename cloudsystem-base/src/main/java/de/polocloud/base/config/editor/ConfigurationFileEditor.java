package de.polocloud.base.config.editor;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public final class ConfigurationFileEditor {

    @SneakyThrows
    public ConfigurationFileEditor(final File file, final ConfigReplace configReplace) {
        final var lines = new ArrayList<String>();

        try (final var bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }

        try (final var fileWriter = new FileWriter(file)) {
            for (final var line : lines) {
                fileWriter.write(configReplace.replace(line) + "\n");
            }
        }

    }

}
