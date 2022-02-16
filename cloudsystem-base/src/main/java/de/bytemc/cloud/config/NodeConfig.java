package de.bytemc.cloud.config;

import de.bytemc.cloud.database.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NodeConfig {

    private final String nodeName;
    private final String hostname;
    private final int port;
    private final DatabaseConfiguration databaseConfiguration;
    private final String javaCommand;

}
