package de.bytemc.cloud.api;

import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;

public interface ICloudAPI {

    IGroupManager getGroupManager();

    IServiceManager getServiceManager();

    ICloudPlayerManager getCloudPlayerManager();

}
