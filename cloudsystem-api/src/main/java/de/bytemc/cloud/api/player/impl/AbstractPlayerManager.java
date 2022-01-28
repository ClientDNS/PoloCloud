package de.bytemc.cloud.api.player.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public abstract class AbstractPlayerManager implements ICloudPlayerManager {

    private final List<ICloudPlayer> allCachedCloudPlayers = Lists.newArrayList();

    public abstract void registerCloudPlayer(UUID uniqueID, String username);

    public abstract void unregisterCloudPlayer(UUID uuid);

    @Override
    public ICloudPlayer getCloudPlayerByNameOrNull(String username) {
        return allCachedCloudPlayers.stream().filter(it -> it.getUsername().equalsIgnoreCase(username)).findAny().orElse(null);
    }

    @Override
    public ICloudPlayer getCloudPlayerByUniqueIdOrNull(UUID uniqueID) {
        return allCachedCloudPlayers.stream().filter(it -> it.getUniqueID().equals(uniqueID)).findAny().orElse(null);
    }
}
