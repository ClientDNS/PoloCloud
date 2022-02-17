package de.polocloud.wrapper.player;

import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.player.CloudPlayerLoginEvent;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.player.CloudPlayerDisconnectPacket;
import de.polocloud.api.network.packet.player.CloudPlayerLoginPacket;
import de.polocloud.api.network.packet.player.CloudPlayerMessagePacket;
import de.polocloud.api.network.packet.player.CloudPlayerUpdatePacket;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.impl.AbstractPlayerManager;
import de.polocloud.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CloudPlayerManager extends AbstractPlayerManager {

    @Override
    public @NotNull List<ICloudPlayer> getAllServicePlayers() {
        return this.getPlayers().stream()
            .filter(it -> it.getServer().getName().equalsIgnoreCase(Wrapper.getInstance().thisService().getName())).collect(Collectors.toList());
    }

    @Override
    public void registerCloudPlayer(@NotNull ICloudPlayer cloudPlayer) {
        this.players.put(cloudPlayer.getUniqueId(), cloudPlayer);
        Wrapper.getInstance().getEventHandler().call(new CloudPlayerLoginEvent(cloudPlayer));
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new CloudPlayerLoginPacket(cloudPlayer.getUsername(),
            cloudPlayer.getUniqueId(), cloudPlayer.getProxyServer().getName()), QueryPacket.QueryState.FIRST_RESPONSE));
    }

    @Override
    public void unregisterCloudPlayer(@NotNull UUID uuid) {
        Wrapper.getInstance().getEventHandler().call(new CloudPlayerDisconnectEvent(this.players.remove(uuid)));
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
