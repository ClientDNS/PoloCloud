package de.bytemc.cloud.config;

import de.bytemc.cloud.api.json.Document;
import de.bytemc.cloud.database.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@Getter
@AllArgsConstructor
public class NodeConfig {

    private static final File FILE = new File("node.json");

    private String nodeName;
    private String hostname;
    private int port;
    private final DatabaseConfiguration databaseConfiguration;

    public static NodeConfig read() {
        if (FILE.exists()) {
            return new Document(FILE).get(NodeConfig.class);
        } else {
            final var nodeConfig = new NodeConfig("node-1", "127.0.0.1", 8876,
                new DatabaseConfiguration("127.0.0.1", 3306, "cloud", "cloud", "password123"));
            new Document(nodeConfig).write(FILE);
            return nodeConfig;
        }
    }

}
