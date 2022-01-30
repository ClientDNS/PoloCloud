package de.bytemc.cloud.api;

import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;
import org.jetbrains.annotations.NotNull;

public interface ICloudAPI {

    /**
     * @return the group manager
     */
    @NotNull IGroupManager getGroupManager();

    /**
     * @return the service manager
     */
    @NotNull IServiceManager getServiceManager();

    /**
     * @return the player manager
     */
    @NotNull ICloudPlayerManager getCloudPlayerManager();

}
