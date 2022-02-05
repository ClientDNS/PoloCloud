package de.bytemc.cloud.wrapper.player;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.QueryPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerCachePacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerUpdatePacket;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;
import de.bytemc.cloud.wrapper.Wrapper;
import de.bytemc.cloud.wrapper.service.ServiceManager;
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
            .filter(it -> it.getServer().getName().equalsIgnoreCase(((ServiceManager) CloudAPI.getInstance().getServiceManager())
                .thisService().getName())).collect(Collectors.toList());
    }

    @Override
    public void registerCloudPlayer(@NotNull UUID uniqueID, @NotNull String username) {
        this.cachedCloudPlayers.put(uniqueID, new SimpleCloudPlayer(uniqueID, username));
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new CloudPlayerLoginPacket(username, uniqueID), QueryPacket.QueryState.FIRST_RESPONSE));
    }

    @Override
    public void unregisterCloudPlayer(@NotNull UUID uuid, @NotNull String username) {
        this.cachedCloudPlayers.remove(uuid);
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new CloudPlayerDisconnectPacket(uuid, username), QueryPacket.QueryState.FIRST_RESPONSE));
    }

    @Override
    public void updateCloudPlayer(@NotNull ICloudPlayer cloudPlayer) {
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new CloudPlayerUpdatePacket(cloudPlayer), QueryPacket.QueryState.FIRST_RESPONSE));
    }

}
