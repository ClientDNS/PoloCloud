package de.polocloud.base.player;

import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.player.CloudPlayerUpdatePacket;
import de.polocloud.base.Base;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.network.packet.player.CloudPlayerMessagePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.impl.AbstractPlayerManager;
import de.polocloud.network.NetworkType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class SimplePlayerManager extends AbstractPlayerManager {

    @Override
    public @NotNull List<ICloudPlayer> getAllServicePlayers() {
        return this.getPlayers();
    }

    @Override
    public void registerCloudPlayer(final @NotNull ICloudPlayer cloudPlayer) {
        this.getAllServicePlayers().add(cloudPlayer);
    }

    @Override
    public void unregisterCloudPlayer(final @NotNull UUID uuid) {
        this.players.remove(uuid);
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
        CloudPlayerUpdatePacket packet = new CloudPlayerUpdatePacket(cloudPlayer, updateReason);
        // update all other nodes and this connected services
        Base.getInstance().getNode().sendPacketToType(new QueryPacket(packet, QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
        // update own service caches
        Base.getInstance().getNode().sendPacketToType(packet, NetworkType.WRAPPER);
        // call event
        Base.getInstance().getEventHandler().call(new CloudPlayerUpdateEvent(cloudPlayer, updateReason));
    }

}
