package de.bytemc.cloud.config;

import com.google.common.collect.Lists;
import de.bytemc.cloud.database.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CloudConfiguration {

    private final String javaCommand = "java";

    private final NodeConfiguration nodeConfiguration = new NodeConfiguration("Node-1", "127.0.0.1", 8876);
    private final List<NodeConfiguration> connectedNodes = Lists.newArrayList();

    private final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration("127.0.0.1", 3306, "cloud", "cloud", "password123");

}
