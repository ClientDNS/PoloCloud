package de.bytemc.cloud;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.command.DefaultCommandSender;
import lombok.Getter;

public class Base extends CloudAPI {

    @Getter private static Base instance;
    @Getter private static final DefaultCommandSender commandSender = new DefaultCommandSender();

    public Base() {
        super(CloudAPITypes.NODE);

        getLoggerProvider().logMessage("§7Cloudsystem » §b@ByteMC §7| §7Developed by: §bHttpMarco §7| Date: §b19.01.2020", LogType.EMPTY);
        getLoggerProvider().logMessage(" ", LogType.EMPTY);


        instance = this;
    }

    @Override
    public IGroupManager getGroupManager() {
        return null;
    }

    @Override
    public IServiceManager getServiceManager() {
        return null;
    }
}
