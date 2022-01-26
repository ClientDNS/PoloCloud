package de.bytemc.cloud.plugin;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.plugin.console.DefaultCommandSender;
import de.bytemc.cloud.plugin.groups.GroupManager;
import de.bytemc.cloud.plugin.network.PluginClient;
import de.bytemc.cloud.plugin.services.ServiceManager;
import lombok.Getter;

@Getter
public class CloudPlugin extends CloudAPI {

    private final DefaultCommandSender commandSender = new DefaultCommandSender();
    private final IGroupManager groupManager;
    private final IServiceManager serviceManager;
    private final PluginClient pluginClient;

    public CloudPlugin() {
        super(CloudAPITypes.SERVICE);

        this.groupManager = new GroupManager();
        this.serviceManager = new ServiceManager();

        this.pluginClient = new PluginClient("pxt-1", "127.0.0.1", 9943);

        CloudAPI.getInstance().getLoggerProvider().logMessage("Successfully started plugin client.", LogType.SUCCESS);
    }
}
