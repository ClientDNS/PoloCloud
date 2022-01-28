package de.bytemc.cloud.api;

import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;

import java.util.UUID;

public interface ICloudAPI {

    IGroupManager getGroupManager();

    IServiceManager getServiceManager();

    ICloudPlayerManager getCloudPlayerManager();


}
