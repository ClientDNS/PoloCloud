package de.bytemc.cloud.plugin.player;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.CloudQueryPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;
import de.bytemc.cloud.plugin.CloudPlugin;
import de.bytemc.cloud.plugin.services.ServiceManager;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CloudPlayerManager extends AbstractPlayerManager {

    @Override
    public List<ICloudPlayer> getAllServicePlayers() {
        return getAllServicePlayers().stream().filter(it -> it.getServer().getName().equalsIgnoreCase(((ServiceManager)CloudPlugin.getInstance().getServiceManager()).thisService().getName())).collect(Collectors.toList());
    }

    @Override
    public void registerCloudPlayer(UUID uniqueID, String username) {
        getAllServicePlayers().add(new SimpleCloudPlayer(uniqueID, username));
        CloudPlugin.getInstance().getPluginClient().sendPacket(new CloudQueryPacket(((ServiceManager)CloudAPI.getInstance().getServiceManager()).thisService().getName(),
            new CloudPlayerLoginPacket(username,uniqueID)));
    }

    @Override
    public void unregisterCloudPlayer(UUID uuid) {
        getAllServicePlayers().remove(getCloudPlayerByUniqueIdOrNull(uuid));
        CloudPlugin.getInstance().getPluginClient().sendPacket(new CloudQueryPacket(((ServiceManager)CloudAPI.getInstance().getServiceManager()).thisService().getName(),
            new CloudPlayerDisconnectPacket(uuid)));
    }
}
