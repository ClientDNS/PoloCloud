package de.polocloud.base.config.editor;

import java.io.*;
import java.util.ArrayList;

public final class ConfigurationFileEditor {

    public ConfigurationFileEditor(final File file, final ConfigReplace configReplace) {
        final var lines = new ArrayList<String>();

        try (final var bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (final var fileWriter = new FileWriter(file)) {
            for (final var line : lines) {
                fileWriter.write(configReplace.replace(line) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
