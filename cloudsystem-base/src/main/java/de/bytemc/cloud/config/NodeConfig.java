package de.bytemc.cloud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.bytemc.cloud.Base;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@Getter @AllArgsConstructor
public class NodeConfig {

    private static final File file = new File("node.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private String nodeName;
    private String hostname;
    private int port;

    @SneakyThrows
    public static NodeConfig read(){

        if(file.exists()) {
            var parser = new JsonParser();
            FileReader fileReader = new FileReader(file.getPath());
            var data = (JsonObject) parser.parse(fileReader);

            return new NodeConfig(data.get("current.node.name").getAsString(), data.get("service.node.hostname").getAsString(), data.get("service.node.port").getAsInt());
        } else {
            var company = new JsonObject();
            company.addProperty("current.node.name", "node-1");
            company.addProperty("current.node.hostname", "127.0.0.1");
            company.addProperty("current.node.port", 8876);

            var fileWriter = new FileWriter(file.getPath());
            fileWriter.write(gson.toJson(company));
            fileWriter.flush();
            fileWriter.close();

            return new NodeConfig("node-1", "127.0.0.1", 8876);
        }
    }

}
