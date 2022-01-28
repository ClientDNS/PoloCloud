package de.bytemc.cloud.api.player;

import java.util.List;
import java.util.UUID;

public interface ICloudPlayerManager {

    List<ICloudPlayer> getAllCachedCloudPlayers();

    List<ICloudPlayer> getAllServicePlayers();

    ICloudPlayer getCloudPlayerByUniqueIdOrNull(UUID uniqueID);

    ICloudPlayer getCloudPlayerByNameOrNull(String username);

    default int getCloudPlayerOnlineAmount(){
        return getAllCachedCloudPlayers().size();
    }

    void registerCloudPlayer(UUID uniqueId, String username);

    void unregisterCloudPlayer(UUID uuid);

}
