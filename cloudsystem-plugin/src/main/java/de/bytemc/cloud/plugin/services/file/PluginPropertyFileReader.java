package de.bytemc.cloud.plugin.services.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.FileReader;

@Getter
public class PluginPropertyFileReader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final String node;
    private final String hostname;
    private final String service;
    private final int port;

    @SneakyThrows
    public PluginPropertyFileReader() {
        final FileReader fileReader = new FileReader("property.json");
        final var data = (JsonObject) JsonParser.parseReader(fileReader);

        this.hostname = data.get("service.node.hostname").getAsString();
        this.port = data.get("service.node.port").getAsInt();
        this.service = data.get("service.name").getAsString();
        this.node = data.get("service.node").getAsString();

        fileReader.close();
    }

}
