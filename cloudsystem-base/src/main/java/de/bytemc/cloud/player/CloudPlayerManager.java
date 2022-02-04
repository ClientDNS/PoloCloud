package de.bytemc.cloud.player;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.QueryPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerUpdatePacket;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;
import de.bytemc.network.cluster.types.NetworkType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class CloudPlayerManager extends AbstractPlayerManager {

    public CloudPlayerManager() {

        //TODO REPLACEMENT

        final INetworkHandler handler = CloudAPI.getInstance().getNetworkHandler();

        handler.registerPacketListener(CloudPlayerLoginPacket.class, (ctx, packet) -> this.registerCloudPlayer(packet.getUuid(), packet.getUsername()));
        handler.registerPacketListener(CloudPlayerDisconnectPacket.class, (ctx, packet) -> this.unregisterCloudPlayer(packet.getUuid(), packet.getName()));
    }

    @Override
    public @NotNull List<ICloudPlayer> getAllServicePlayers() {
        return this.getAllCachedCloudPlayers();
    }

    @Override
    public void registerCloudPlayer(final @NotNull UUID uniqueID, final @NotNull String username) {
        this.getAllServicePlayers().add(new SimpleCloudPlayer(uniqueID, username));
    }

    @Override
    public void unregisterCloudPlayer(final @NotNull UUID uuid, final @NotNull String name) {
        this.getAllServicePlayers().remove(getCloudPlayerByUniqueIdOrNull(uuid));
    }

    @Override
    public void updateCloudPlayer(ICloudPlayer cloudPlayer) {
        CloudPlayerUpdatePacket packet = new CloudPlayerUpdatePacket(cloudPlayer);
        //update all other nodes and this connected services
        Base.getInstance().getNode().sendPacketToType(new QueryPacket(packet, QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
        //update own service caches
        Base.getInstance().getNode().sendPacketToType(packet, NetworkType.SERVICE);
    }
}
