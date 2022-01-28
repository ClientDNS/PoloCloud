package de.bytemc.cloud.plugin.player;

import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;
import de.bytemc.cloud.plugin.CloudPlugin;
import de.bytemc.cloud.plugin.services.ServiceManager;

import java.util.List;
import java.util.stream.Collectors;

public class CloudPlayerManager extends AbstractPlayerManager {

    @Override
    public List<ICloudPlayer> getAllServicePlayers() {
        return getAllServicePlayers().stream().filter(it -> it.getServer().getName().equalsIgnoreCase(((ServiceManager)CloudPlugin.getInstance().getServiceManager()).thisService().getName())).collect(Collectors.toList());
    }
}
