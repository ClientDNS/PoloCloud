package de.bytemc.cloud.config;

import de.bytemc.cloud.database.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NodeConfig {

    private String nodeName;
    private String hostname;
    private int port;
    private final DatabaseConfiguration databaseConfiguration;

}
