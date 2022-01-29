package de.bytemc.cloud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@Getter
@AllArgsConstructor
public class NodeConfig {

    private static final File FILE = new File("node.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private String nodeName;
    private String hostname;
    private int port;

    @SneakyThrows
    public static NodeConfig read() {

        if (FILE.exists()) {
            final FileReader fileReader = new FileReader(FILE.getPath());
            var data = (JsonObject) JsonParser.parseReader(fileReader);

            return new NodeConfig(data.get("current.node.name").getAsString(), data.get("current.node.hostname").getAsString(),
                data.get("current.node.port").getAsInt());
        } else {
            final var company = new JsonObject();
            company.addProperty("current.node.name", "node-1");
            company.addProperty("current.node.hostname", "127.0.0.1");
            company.addProperty("current.node.port", 8876);

            final var fileWriter = new FileWriter(FILE.getPath());
            fileWriter.write(GSON.toJson(company));
            fileWriter.flush();
            fileWriter.close();

            return new NodeConfig("node-1", "127.0.0.1", 8876);
        }
    }

}
