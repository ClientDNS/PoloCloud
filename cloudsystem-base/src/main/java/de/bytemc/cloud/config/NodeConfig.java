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
    public static NodeConfig NODE_CONFIG;

    private String nodeName;
    private String hostname;
    private int port;
    private final DatabaseConfiguration databaseConfiguration;

    static {
        read();
    }

    public static void read() {
        if (FILE.exists()) {
            NODE_CONFIG = new Document(FILE).get(NodeConfig.class);
        } else {
            final var nodeConfig = new NodeConfig("node-1", "127.0.0.1", 8876,
                new DatabaseConfiguration("127.0.0.1", 3306, "cloud", "cloud", "password123"));
            new Document(nodeConfig).write(FILE);
            NODE_CONFIG = nodeConfig;
        }
    }

    public static NodeConfig get() {
        return NODE_CONFIG;
    }

}
