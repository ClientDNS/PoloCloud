package de.bytemc.cloud.player;

import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;

import java.util.List;
import java.util.UUID;

public class CloudPlayerManager extends AbstractPlayerManager {

    @Override
    public List<ICloudPlayer> getAllServicePlayers() {
        return getAllCachedCloudPlayers();
    }


    @Override
    public void registerCloudPlayer(UUID uniqueID, String username) {
        //send packet to node
        getAllServicePlayers().add(new SimpleCloudPlayer(uniqueID, username));
    }

    @Override
    public void unregisterCloudPlayer(UUID uuid) {
        //send packet to node

    }
}
