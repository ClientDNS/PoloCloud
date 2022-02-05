package de.bytemc.cloud.api.player.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudPlayerDisconnectEvent;
import de.bytemc.cloud.api.events.events.CloudPlayerLoginEvent;
import de.bytemc.cloud.api.events.events.CloudPlayerUpdateEvent;
import de.bytemc.cloud.api.events.events.CloudServiceRemoveEvent;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerUpdatePacket;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Setter
@Getter
public abstract class AbstractPlayerManager implements ICloudPlayerManager {

    private List<ICloudPlayer> allCachedCloudPlayers = Lists.newCopyOnWriteArrayList();

    public AbstractPlayerManager() {
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(CloudPlayerUpdatePacket.class, (ctx, packet) -> {
            ICloudPlayer cloudPlayer = getCloudPlayerByUniqueIdOrNull(packet.getUuid());
            Objects.requireNonNull(cloudPlayer, "Updated cloud player is null.");

            cloudPlayer.setProxyServer(packet.getProxyServer());
            cloudPlayer.setServer(packet.getServer());
            CloudAPI.getInstance().getEventHandler().call(new CloudPlayerUpdateEvent(cloudPlayer));
        });

        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(CloudPlayerLoginPacket.class, (ctx, packet) -> {
            final ICloudPlayer cloudPlayer = new SimpleCloudPlayer(packet.getUuid(), packet.getUsername());

            this.allCachedCloudPlayers.add(cloudPlayer);
            CloudAPI.getInstance().getEventHandler().call(new CloudPlayerLoginEvent(cloudPlayer));
        });

        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(CloudPlayerDisconnectPacket.class, (ctx, packet) -> {
            this.getCloudPlayer(packet.getUuid()).ifPresent(cloudPlayer -> {
                this.allCachedCloudPlayers.remove(cloudPlayer);
                CloudAPI.getInstance().getEventHandler().call(new CloudPlayerDisconnectEvent(cloudPlayer));
            });
        });

        CloudAPI.getInstance().getEventHandler().registerEvent(CloudServiceRemoveEvent.class, event ->
            this.allCachedCloudPlayers.forEach(player -> {
                if (player.getProxyServer().getName().equals(event.getService()))
                    this.allCachedCloudPlayers.remove(player);
            })
        );

    }

    public abstract void registerCloudPlayer(@NotNull UUID uniqueID, @NotNull String username);

    public abstract void unregisterCloudPlayer(@NotNull UUID uuid, @NotNull String name);

    public abstract void updateCloudPlayer(@NotNull ICloudPlayer cloudPlayer);

    @Override
    public @NotNull Optional<ICloudPlayer> getCloudPlayer(final @NotNull UUID uniqueId) {
        return  this.allCachedCloudPlayers.stream().filter(it -> it.getUniqueId().equals(uniqueId)).findAny();
    }

    @Override
    public @NotNull Optional<ICloudPlayer> getCloudPlayer(final @NotNull String username) {
        return this.allCachedCloudPlayers.stream().filter(it -> it.getUsername().equalsIgnoreCase(username)).findAny();
    }

    @Override
    public ICloudPlayer getCloudPlayerByNameOrNull(@NotNull String username) {
        return this.getCloudPlayer(username).orElse(null);
    }

    @Override
    public ICloudPlayer getCloudPlayerByUniqueIdOrNull(@NotNull UUID uniqueId) {
        return this.getCloudPlayer(uniqueId).orElse(null);
    }

}
