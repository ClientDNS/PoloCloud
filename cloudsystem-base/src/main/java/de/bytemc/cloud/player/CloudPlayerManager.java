package de.bytemc.cloud.player;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;

import java.util.List;
import java.util.UUID;

public class CloudPlayerManager extends AbstractPlayerManager {

    public CloudPlayerManager() {
        INetworkHandler handler = CloudAPI.getInstance().getNetworkHandler();
        handler.registerPacketListener(CloudPlayerLoginPacket.class, (ctx, packet) -> {
            registerCloudPlayer(packet.getUuid(), packet.getUsername());
        });
        handler.registerPacketListener(CloudPlayerDisconnectPacket.class, (ctx, packet) -> {
            unregisterCloudPlayer(packet.getUuid(), packet.getName());
        });
    }

    @Override
    public List<ICloudPlayer> getAllServicePlayers() {
        return this.getAllCachedCloudPlayers();
    }

    @Override
    public void registerCloudPlayer(UUID uniqueID, String username) {
        this.getAllServicePlayers().add(new SimpleCloudPlayer(uniqueID, username));
    }

    @Override
    public void unregisterCloudPlayer(UUID uuid, String name) {
        this.getAllServicePlayers().remove(getCloudPlayerByUniqueIdOrNull(uuid));
    }

}
