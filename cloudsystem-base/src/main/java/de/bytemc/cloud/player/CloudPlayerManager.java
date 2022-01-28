package de.bytemc.cloud.player;

import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.AbstractPlayerManager;

import java.util.List;

public class CloudPlayerManager extends AbstractPlayerManager {

    @Override
    public List<ICloudPlayer> getAllServicePlayers() {
        return getAllCachedCloudPlayers();
    }
}
