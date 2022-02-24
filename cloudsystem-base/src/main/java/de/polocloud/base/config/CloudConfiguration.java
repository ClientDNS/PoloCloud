package de.polocloud.base.config;

import de.polocloud.database.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CloudConfiguration {

    private final NodeConfiguration nodeConfiguration = new NodeConfiguration("Node-1", "127.0.0.1", 8876);
    private final List<NodeConfiguration> connectedNodes = new ArrayList<>();

    private final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();

    private final String javaCommand = "java";

}
