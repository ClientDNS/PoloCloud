package de.bytemc.cloud.api.player;

import de.bytemc.cloud.api.services.IService;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface ICloudPlayer {

    /**
     * @return the username of the player
     */
    @NotNull String getUsername();

    /**
     * @return the unique id of the player
     */
    @NotNull UUID getUniqueId();

    /**
     * @return the unique id of the player
     */
    @Deprecated(forRemoval = true)
    @NotNull UUID getUniqueID();

    /**
     * @return the proxy server who the player is
     */
    IService getProxyServer();

    /**
     * @return the server who the player is
     */
    IService getServer();

}
