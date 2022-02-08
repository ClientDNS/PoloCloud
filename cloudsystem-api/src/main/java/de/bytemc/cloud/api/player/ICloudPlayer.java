package de.bytemc.cloud.api.player;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerKickPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerSendServicePacket;
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
    default void connect(@NotNull IService service) {
        assert getProxyServer() != null;
        this.getProxyServer().sendPacket(new CloudPlayerSendServicePacket(getUniqueId(),service.getName()));
    }

    /**
     * kicks the player
     */
    default void kick() {
        kick("");
    }

    /**
     * kicks the player with a reason
     * @param reason the reason of the kick
     */
    default void kick(@NotNull String reason) {
        assert getProxyServer() != null;
        this.getProxyServer().sendPacket(new CloudPlayerKickPacket(getUniqueId(), getProxyServer().getName(), reason));
    }

    /**
     * send the player a message over all proxies
     * @param message the message
     */
    default void sendMessage(@NotNull String message){
        assert getProxyServer() != null;
        CloudAPI.getInstance().getCloudPlayerManager().sendCloudPlayerMessage(this, message);
    }

    /**
     * updates the properties of the player
     */
    void update();

}
