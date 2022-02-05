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
     * @return the proxy server who the player is
     */
    IService getProxyServer();

    /**
     * sets the proxy server of the player
     * @param service the service to set
     */
    void setProxyServer(@NotNull IService service);

    /**
     * @return the server who the player is
     */
    IService getServer();

    /**
     * sets the server of the player
     * @param service the service to set
     */
    void setServer(@NotNull IService service);

    /**
     * connects the player to a service
     * @param service the service to connect
     */
    void connect(@NotNull IService service);

    /**
     * kicks the player
     */
    void kick();

    /**
     * kicks the player with a reason
     * @param reason the reason of the kick
     */
    void kick(@NotNull String reason);

    /**
     * updates the properties of the player
     */
    void update();

}
