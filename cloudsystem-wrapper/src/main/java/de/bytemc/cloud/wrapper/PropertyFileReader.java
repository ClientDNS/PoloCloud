package de.bytemc.cloud.wrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;

public class PropertyFileReader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private String node;
    private String hostname;
    private String service;
    private int port;

    public PropertyFileReader() {
        try (final FileReader fileReader = new FileReader("property.json")) {
            final var data = (JsonObject) JsonParser.parseReader(fileReader);

            this.hostname = data.get("service.node.hostname").getAsString();
            this.port = data.get("service.node.port").getAsInt();
            this.service = data.get("service.name").getAsString();
            this.node = data.get("service.node").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNode() {
        return this.node;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getService() {
        return this.service;
    }

    public int getPort() {
        return this.port;
    }

}
