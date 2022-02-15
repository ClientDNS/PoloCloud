package de.bytemc.cloud.wrapper.player;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudPlayerDisconnectEvent;
import de.bytemc.cloud.api.events.events.CloudPlayerLoginEvent;
import de.bytemc.cloud.api.events.events.CloudPlayerUpdateEvent;
import de.bytemc.cloud.api.network.packets.QueryPacket;
import de.bytemc.cloud.api.network.packets.player.*;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;
import de.bytemc.cloud.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class CloudPlayerManager extends AbstractPlayerManager {

    public CloudPlayerManager() {
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(CloudPlayerCachePacket.class, (ctx, packet) -> {
            final Map<UUID, ICloudPlayer> cloudPlayerMap = new ConcurrentHashMap<>();
            packet.getCloudPlayers().forEach(cloudPlayer -> cloudPlayerMap.put(cloudPlayer.getUniqueId(), cloudPlayer));
            this.setCachedCloudPlayers(cloudPlayerMap);
        });
    }

    @Override
    public @NotNull List<ICloudPlayer> getAllServicePlayers() {
        return this.getAllCachedCloudPlayers().stream()
            .filter(it -> it.getServer().getName().equalsIgnoreCase(Wrapper.getInstance().thisService().getName())).collect(Collectors.toList());
    }

    @Override
    public void registerCloudPlayer(@NotNull ICloudPlayer cloudPlayer) {
        this.cachedCloudPlayers.put(cloudPlayer.getUniqueId(), cloudPlayer);
        Wrapper.getInstance().getEventHandler().call(new CloudPlayerLoginEvent(cloudPlayer));
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new CloudPlayerLoginPacket(cloudPlayer.getUsername(),
            cloudPlayer.getUniqueId(), cloudPlayer.getProxyServer().getName()), QueryPacket.QueryState.FIRST_RESPONSE));
    }

    @Override
    public void unregisterCloudPlayer(@NotNull UUID uuid) {
        Wrapper.getInstance().getEventHandler().call(new CloudPlayerDisconnectEvent(this.cachedCloudPlayers.remove(uuid)));
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new CloudPlayerDisconnectPacket(uuid), QueryPacket.QueryState.FIRST_RESPONSE));
    }

    @Override
    public void sendCloudPlayerMessage(@NotNull ICloudPlayer cloudPlayer, @NotNull String message) {
        cloudPlayer.getProxyServer().sendPacket(new CloudPlayerMessagePacket(cloudPlayer.getUniqueId(), message));
    }

    @Override
    public void updateCloudPlayer(@NotNull ICloudPlayer cloudPlayer) {
        this.updateCloudPlayer(cloudPlayer, CloudPlayerUpdateEvent.UpdateReason.UNKNOWN);
    }

    @Override
    public void updateCloudPlayer(@NotNull ICloudPlayer cloudPlayer, @NotNull CloudPlayerUpdateEvent.UpdateReason updateReason) {
        Wrapper.getInstance().getClient()
            .sendPacket(new QueryPacket(new CloudPlayerUpdatePacket(cloudPlayer, updateReason), QueryPacket.QueryState.FIRST_RESPONSE));
        Wrapper.getInstance().getEventHandler().call(new CloudPlayerUpdateEvent(cloudPlayer, updateReason));
    }


}
