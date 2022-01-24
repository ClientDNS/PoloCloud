package de.bytemc.cloud;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.command.DefaultCommandSender;
import de.bytemc.cloud.groups.SimpleGroupManager;
import de.bytemc.cloud.node.BaseNode;
import de.bytemc.cloud.services.ServiceManager;
import lombok.Getter;

@Getter
public class Base extends CloudAPI {

    @Getter private static Base instance;
    @Getter private final DefaultCommandSender commandSender = new DefaultCommandSender();

    private BaseNode node;
    private IGroupManager groupManager;
    private IServiceManager serviceManager;

    public Base() {
        super(CloudAPITypes.NODE);

        getLoggerProvider().logMessage("§7Cloudsystem » §b@ByteMC §7| §7Developed by: §bHttpMarco §7| Date: §b19.01.2020", LogType.EMPTY);
        getLoggerProvider().logMessage(" ", LogType.EMPTY);


        this.groupManager = new SimpleGroupManager();
        this.serviceManager = new ServiceManager();
        this.node = new BaseNode();

        instance = this;
    }

}
