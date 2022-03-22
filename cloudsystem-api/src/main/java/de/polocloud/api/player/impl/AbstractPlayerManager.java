package de.polocloud.api.player.impl;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.player.CloudPlayerLoginEvent;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.event.service.CloudServiceRemoveEvent;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.network.packet.player.CloudPlayerDisconnectPacket;
import de.polocloud.api.network.packet.player.CloudPlayerLoginPacket;
import de.polocloud.api.network.packet.player.CloudPlayerUpdatePacket;
import de.polocloud.api.player.CloudPlayer;
import de.polocloud.api.player.PlayerManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractPlayerManager implements PlayerManager {

    protected Map<UUID, CloudPlayer> players;

    public AbstractPlayerManager() {

        final var packetHandler = CloudAPI.getInstance().getPacketHandler();
        final var eventHandler = CloudAPI.getInstance().getEventHandler();

        packetHandler.registerPacketListener(CloudPlayerUpdatePacket.class, (channelHandlerContext, packet) ->
            this.getCloudPlayer(packet.getUuid()).ifPresent(cloudPlayer -> {
                cloudPlayer.setServer(packet.getServer());
                eventHandler.call(new CloudPlayerUpdateEvent(cloudPlayer, packet.getUpdateReason()));
            }));

        packetHandler.registerPacketListener(CloudPlayerLoginPacket.class, (channelHandlerContext, packet) ->
            CloudAPI.getInstance().getServiceManager().getService(packet.getProxyServer()).ifPresentOrElse(service -> {
                final var cloudPlayer = new SimpleCloudPlayer(packet.getUuid(), packet.getUsername(), service);
                this.players.put(packet.getUuid(), cloudPlayer);
                eventHandler.call(new CloudPlayerLoginEvent(cloudPlayer));
            }, () -> CloudAPI.getInstance().getLogger()
                .log("Proxy " + packet.getProxyServer() + " not found for player "
                    + packet.getUsername() + ":" + packet.getUuid(), LogType.ERROR)));

        packetHandler.registerPacketListener(CloudPlayerDisconnectPacket.class, (channelHandlerContext, packet) ->
            this.getCloudPlayer(packet.getUniqueId()).ifPresent(cloudPlayer -> {
                this.players.remove(cloudPlayer.getUniqueId());
                eventHandler.call(new CloudPlayerDisconnectEvent(cloudPlayer));
            }));

        eventHandler.registerEvent(CloudServiceRemoveEvent.class, event ->
            this.players.values().forEach(player -> {
                if (player.getProxyServer() == null || player.getProxyServer().getName().equals(event.getService()))
                    this.players.remove(player.getUniqueId());
            })
        );

    }

    public void setPlayers(final Map<UUID, CloudPlayer> players) {
        this.players = players;
    }

    @Override
    public @NotNull List<CloudPlayer> getPlayers() {
        return Arrays.asList(this.players.values().toArray(new CloudPlayer[0]));
    }

    @Override
    public @NotNull Optional<CloudPlayer> getCloudPlayer(final @NotNull UUID uniqueId) {
        return Optional.ofNullable(this.players.get(uniqueId));
    }

    @Override
    public @NotNull Optional<CloudPlayer> getCloudPlayer(final @NotNull String username) {
        return this.players.values().stream().filter(it -> it.getUsername().equalsIgnoreCase(username)).findAny();
    }

    @Override
    public CloudPlayer getCloudPlayerByNameOrNull(@NotNull String username) {
        return this.getCloudPlayer(username).orElse(null);
    }

    @Override
    public CloudPlayer getCloudPlayerByUniqueIdOrNull(@NotNull UUID uniqueId) {
        return this.getCloudPlayer(uniqueId).orElse(null);
    }

}
