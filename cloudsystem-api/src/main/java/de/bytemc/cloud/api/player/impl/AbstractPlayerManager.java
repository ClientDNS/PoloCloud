package de.bytemc.cloud.api.player.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public abstract class AbstractPlayerManager implements ICloudPlayerManager {

    private final List<ICloudPlayer> allCachedCloudPlayers = Lists.newArrayList();

    public abstract void registerCloudPlayer(@NotNull UUID uniqueID, @NotNull String username);

    public abstract void unregisterCloudPlayer(@NotNull UUID uuid, @NotNull String name);

    @Override
    public ICloudPlayer getCloudPlayerByNameOrNull(@NotNull String username) {
        return this.allCachedCloudPlayers.stream().filter(it -> it.getUsername().equalsIgnoreCase(username)).findAny().orElse(null);
    }

    @Override
    public ICloudPlayer getCloudPlayerByUniqueIdOrNull(@NotNull UUID uniqueId) {
        return this.allCachedCloudPlayers.stream().filter(it -> it.getUniqueId().equals(uniqueId)).findAny().orElse(null);
    }

}
