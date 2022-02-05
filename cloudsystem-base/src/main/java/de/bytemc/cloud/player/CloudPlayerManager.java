package de.bytemc.cloud.player;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.network.packets.QueryPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerUpdatePacket;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;
import de.bytemc.network.cluster.types.NetworkType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class CloudPlayerManager extends AbstractPlayerManager {

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
    public void updateCloudPlayer(@NotNull ICloudPlayer cloudPlayer) {
        CloudPlayerUpdatePacket packet = new CloudPlayerUpdatePacket(cloudPlayer);
        //update all other nodes and this connected services
        Base.getInstance().getNode().sendPacketToType(new QueryPacket(packet, QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
        //update own service caches
        Base.getInstance().getNode().sendPacketToType(packet, NetworkType.SERVICE);
    }
}
