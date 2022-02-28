package de.polocloud.wrapper.player;

import de.polocloud.api.event.player.CloudPlayerDisconnectEvent;
import de.polocloud.api.event.player.CloudPlayerLoginEvent;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.player.*;
import de.polocloud.api.player.CloudPlayer;
import de.polocloud.api.player.impl.AbstractPlayerManager;
import de.polocloud.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class CloudPlayerManager extends AbstractPlayerManager {

    @Override
    public @NotNull List<CloudPlayer> getAllServicePlayers() {
        return this.getPlayers().stream().filter(it -> it.getServer().getName().equals(Wrapper.getInstance().thisService().getName())).toList();
    }

    @Override
    public void registerCloudPlayer(@NotNull CloudPlayer cloudPlayer) {
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
    public void sendCloudPlayerMessage(@NotNull CloudPlayer cloudPlayer, @NotNull String message) {
        cloudPlayer.getProxyServer().sendPacket(new CloudPlayerMessagePacket(cloudPlayer.getUniqueId(), message));
    }

    @Override
    public void updateCloudPlayer(@NotNull CloudPlayer cloudPlayer) {
        this.updateCloudPlayer(cloudPlayer, CloudPlayerUpdateEvent.UpdateReason.UNKNOWN);
    }

    @Override
    public void updateCloudPlayer(@NotNull CloudPlayer cloudPlayer, @NotNull CloudPlayerUpdateEvent.UpdateReason updateReason) {
        Wrapper.getInstance().getClient()
            .sendPacket(new QueryPacket(new CloudPlayerUpdatePacket(cloudPlayer, updateReason), QueryPacket.QueryState.FIRST_RESPONSE));
        Wrapper.getInstance().getEventHandler().call(new CloudPlayerUpdateEvent(cloudPlayer, updateReason));
    }

}
