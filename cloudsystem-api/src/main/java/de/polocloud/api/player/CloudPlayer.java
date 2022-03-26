package de.polocloud.api.player;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.network.packet.player.CloudPlayerKickPacket;
import de.polocloud.api.network.packet.player.CloudPlayerSendServicePacket;
import de.polocloud.api.player.impl.SimpleCloudPlayer;
import de.polocloud.api.service.CloudService;
import de.polocloud.network.packet.NetworkBuf;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface CloudPlayer {

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
    CloudService getProxyServer();

    /**
     * @return the server who the player is
     */
    CloudService getServer();

    /**
     * sets the server of the player
     *
     * @param service the service to set
     */
    void setServer(@NotNull CloudService service);

    /**
     * connects the player to a service
     *
     * @param service the service to connect
     */
    default void connect(@NotNull String service) {
        this.getProxyServer().sendPacket(new CloudPlayerSendServicePacket(this.getUniqueId(), service));
    }

    /**
     * connects the player to a service
     *
     * @param service the service to connect
     */
    default void connect(@NotNull CloudService service) {
        this.connect(service.getName());
    }

    /**
     * kicks the player
     */
    default void kick() {
        kick("");
    }

    /**
     * kicks the player with a reason
     *
     * @param reason the reason of the kick
     */
    default void kick(@NotNull String reason) {
        assert getProxyServer() != null;
        this.getProxyServer().sendPacket(new CloudPlayerKickPacket(getUniqueId(), getProxyServer().getName(), reason));
    }

    /**
     * send the player a message over all proxies
     *
     * @param message the message
     */
    default void sendMessage(@NotNull String message) {
        assert this.getProxyServer() != null;
        CloudAPI.getInstance().getPlayerManager().sendCloudPlayerMessage(this, message);
    }

    /**
     * updates the properties of the player
     */
    void update();

    /**
     * updates the properties of the player
     *
     * @param updateReason the reason of the update
     */
    void update(@NotNull CloudPlayerUpdateEvent.UpdateReason updateReason);

    /**
     * writes the player to a network buf
     */
    default void write(@NotNull NetworkBuf networkBuf) {
        networkBuf
            .writeUUID(this.getUniqueId())
            .writeString(this.getUsername())
            .writeString(this.getProxyServer().getName())
            .writeString(this.getServer().getName());
    }

    /**
     * reads a player from a network buf
     */
    static CloudPlayer read(@NotNull NetworkBuf networkBuf) {
        return new SimpleCloudPlayer(
            networkBuf.readUUID(),
            networkBuf.readString(),
            CloudAPI.getInstance().getServiceManager().getService(networkBuf.readString()).orElse(null),
            CloudAPI.getInstance().getServiceManager().getService(networkBuf.readString()).orElse(null));
    }

}
