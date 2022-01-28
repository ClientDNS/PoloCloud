package de.bytemc.cloud.api.player;

import java.util.List;

public interface ICloudPlayerManager {

    List<ICloudPlayer> getAllCachedCloudPlayers();

    List<ICloudPlayer> getAllServicePlayers();

    default int getCloudPlayerOnlineAmount(){
        return getAllCachedCloudPlayers().size();
    }

}
