package de.bytemc.cloud.plugin.player;

import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;
import de.bytemc.cloud.plugin.CloudPlugin;
import de.bytemc.cloud.plugin.services.ServiceManager;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CloudPlayerManager extends AbstractPlayerManager {

    private ServiceManager serviceManager;

    @Override
    public List<ICloudPlayer> getAllServicePlayers() {
        return this.getAllCachedCloudPlayers().stream().filter(it -> it.getServer().getName().equalsIgnoreCase(serviceManager.thisService().getName())).collect(Collectors.toList());
    }

    @Override
    public void registerCloudPlayer(UUID uniqueID, String username) {
        getAllCachedCloudPlayers().add(new SimpleCloudPlayer(uniqueID, username));
        CloudPlugin.getInstance().getPluginClient().sendPacket(new CloudPlayerLoginPacket(username, uniqueID, serviceManager.thisService().getName()));
    }

    @Override
    public void unregisterCloudPlayer(UUID uuid, String username) {
        getAllCachedCloudPlayers().remove(getCloudPlayerByUniqueIdOrNull(uuid));
        CloudPlugin.getInstance().getPluginClient().sendPacket(new CloudPlayerDisconnectPacket(uuid, username));
    }

}
