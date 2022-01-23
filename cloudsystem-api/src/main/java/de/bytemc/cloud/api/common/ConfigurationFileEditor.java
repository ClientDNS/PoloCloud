package de.bytemc.cloud.api.common;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigurationFileEditor {

    private final File file;

    private final List<String> listWithSpaces;

    private final List<String> lines;
    private final Map<String, String> keyToValues;

    private final ConfigSplitSpacer splitSpacer;

    @SneakyThrows
    public ConfigurationFileEditor(File file,ConfigSplitSpacer splitSpacer) {
        this.listWithSpaces = Files.readLines(file, StandardCharsets.UTF_8);
        this.lines = this.listWithSpaces.stream().map(it -> removeFirstSpaces(it)).collect(Collectors.toList());

        this.file = file;

        this.keyToValues = Maps.newConcurrentMap();
        this.splitSpacer = splitSpacer;

        loadAllConfigOptions();
    }

    public void loadAllConfigOptions() {
        lines.stream().filter(it -> it.contains(splitSpacer.getSplit())).map(it -> it.split(": ")).forEach(it -> keyToValues.put(it[0], it[1]));
    }

    public String getValue(String key) {
        return this.keyToValues.get(key);
    }

    public void setValue(String key, String value) {
        keyToValues.put(key, value);
    }

    public void saveFile() {
        String[] property = lines.toArray(new String[]{});

        keyToValues.forEach((key, value) -> {
            String line = key + splitSpacer.getSplit() + value;
            int index = getIndexFromLine(line);
            property[index] = line;
        });

        File file = new File("config.yml");
        file.delete();
        try {
            if (!file.exists()) file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            for (String line : property) {
                int index = getIndexFromLine(line);
                fileWriter.write(getStringWithSpaces(getAmountOfStartSpacesInLine(listWithSpaces.get(index))) + line + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getIndexFromLine(String value) {
        int amountOfIndex = 0;
        for (String line : lines) {
            if (value.contains(splitSpacer.getSplit())) {
                if (value.split(splitSpacer.getSplit())[0].equals(removeFirstSpaces(line).split(splitSpacer.getSplit())[0])) {
                    return amountOfIndex;
                }
            }else {
                if (value.equals(removeFirstSpaces(line))) {
                    return amountOfIndex;
                }
            }
            amountOfIndex++;
        }
        return -1;
    }

    public int getAmountOfStartSpacesInLine(String line) {
        String lines = line;
        int amountOfSpaces = 0;
        while (lines.startsWith(" ")) {
            lines = lines.substring(1);
            amountOfSpaces++;
        }
        return amountOfSpaces;
    }

    public String removeFirstSpaces(String string) {
        return string.substring(getAmountOfStartSpacesInLine(string));
    }

    private String getStringWithSpaces(int amount) {
        return " ".repeat(amount);
    }


}
