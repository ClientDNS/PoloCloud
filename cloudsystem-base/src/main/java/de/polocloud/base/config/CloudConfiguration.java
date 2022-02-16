package de.polocloud.base.config;

import com.google.common.collect.Lists;
import de.polocloud.base.database.DatabaseConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CloudConfiguration {

    private final NodeConfiguration nodeConfiguration = new NodeConfiguration("Node-1", "127.0.0.1", 8876);
    private final List<NodeConfiguration> connectedNodes = Lists.newArrayList();

    private final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration("127.0.0.1", 3306, "cloud", "cloud", "password123");

    private final String javaCommand = "java";

}
