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

    void setProxyServer(IService service);

    /**
     * @return the server who the player is
     */
    IService getServer();

    void setServer(IService service);


    void connect(IService service);

    void kick();

    void kick(String reason);

    void update();


}
