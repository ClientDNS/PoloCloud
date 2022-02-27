package de.polocloud.base.config;

import de.polocloud.database.DatabaseConfiguration;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CloudConfiguration {

    private final NodeConfiguration nodeConfiguration;
    private final List<NodeConfiguration> connectedNodes;
    private final DatabaseConfiguration databaseConfiguration;
    private final String javaCommand;
    private final int minecraftStartPort;
    private final int proxyStartPort;
    private final boolean checkForUpdate;
    private final List<String> jvmFlags;

    // default parameters
    public CloudConfiguration() {
        this.nodeConfiguration = new NodeConfiguration("Node-1", "127.0.0.1", 8876);
        this.connectedNodes = new ArrayList<>();
        this.databaseConfiguration = new DatabaseConfiguration();
        this.javaCommand = "java";
        this.minecraftStartPort = 30000;
        this.proxyStartPort = 25565;
        this.checkForUpdate = true;
        this.jvmFlags = new ArrayList<>();
    }

}
