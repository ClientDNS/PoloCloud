package de.bytemc.cloud.api.player.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerUpdatePacket;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public abstract class AbstractPlayerManager implements ICloudPlayerManager {

    private final List<ICloudPlayer> allCachedCloudPlayers = Lists.newArrayList();

    public AbstractPlayerManager() {
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(CloudPlayerUpdatePacket.class, (ctx, packet) -> {
            ICloudPlayer cloudPlayer = getCloudPlayerByUniqueIdOrNull(packet.getUuid());
            Objects.requireNonNull(cloudPlayer, "Updated cloud player is null.");

            cloudPlayer.setProxyServer(packet.getProxyServer());
            cloudPlayer.setServer(packet.getServer());

            //TODO
            System.out.println("CloudPlayer update: " + packet.getUuid() + ";" + cloudPlayer.getProxyServer().getName() + ";" + cloudPlayer.getServer().getName());

        });
    }

    public abstract void registerCloudPlayer(@NotNull UUID uniqueID, @NotNull String username);

    public abstract void unregisterCloudPlayer(@NotNull UUID uuid, @NotNull String name);

    public abstract void updateCloudPlayer(ICloudPlayer cloudPlayer);

    @Override
    public ICloudPlayer getCloudPlayerByNameOrNull(@NotNull String username) {
        return this.allCachedCloudPlayers.stream().filter(it -> it.getUsername().equalsIgnoreCase(username)).findAny().orElse(null);
    }

    @Override
    public ICloudPlayer getCloudPlayerByUniqueIdOrNull(@NotNull UUID uniqueId) {
        return this.allCachedCloudPlayers.stream().filter(it -> it.getUniqueId().equals(uniqueId)).findAny().orElse(null);
    }

}
