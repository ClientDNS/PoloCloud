package de.bytemc.cloud.plugin;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.plugin.console.DefaultProxyCommandSender;
import de.bytemc.cloud.plugin.groups.GroupManager;
import de.bytemc.cloud.plugin.network.PluginClient;
import de.bytemc.cloud.plugin.player.CloudPlayerManager;
import de.bytemc.cloud.plugin.services.ServiceManager;
import de.bytemc.cloud.plugin.services.file.PluginPropertyFileReader;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CloudPlugin extends CloudAPI {

    @Setter private static DefaultProxyCommandSender commandSender;

    @Getter private static CloudPlugin instance;

    private final IGroupManager groupManager;
    private final IServiceManager serviceManager;
    private final ICloudPlayerManager cloudPlayerManager;
    private PluginClient pluginClient;
    private IPlugin plugin;

    public CloudPlugin(IPlugin plugin) {
        super(CloudAPITypes.SERVICE);

        instance = this;

        this.plugin = plugin;
        var property = new PluginPropertyFileReader();

        this.groupManager = new GroupManager();
        this.serviceManager = new ServiceManager(property);
        this.cloudPlayerManager = new CloudPlayerManager();

        pluginClient = new PluginClient(property.getService(), property.getHostname(), property.getPort());

        CloudAPI.getInstance().getLoggerProvider().logMessage("Successfully started plugin client.", LogType.SUCCESS);
    }

    @Override
    public DefaultProxyCommandSender getCommandSender() {
        return commandSender;
    }
}
