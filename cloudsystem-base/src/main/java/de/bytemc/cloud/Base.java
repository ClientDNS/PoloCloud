package de.bytemc.cloud;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.command.DefaultCommandSender;
import lombok.Getter;

public class Base extends CloudAPI {

    @Getter private static Base instance;
    @Getter private static final DefaultCommandSender commandSender = new DefaultCommandSender();

    public Base() {
        super(CloudAPITypes.NODE);

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
