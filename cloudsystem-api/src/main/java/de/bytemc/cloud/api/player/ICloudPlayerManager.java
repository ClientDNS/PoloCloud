package de.bytemc.cloud.api.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface ICloudPlayerManager {

    /**
     * @return a list of all cloud players
     */
    @NotNull List<ICloudPlayer> getAllCachedCloudPlayers();

    /**
     * @return a list of all cloud players on the service
     */
    @NotNull List<ICloudPlayer> getAllServicePlayers();

    /**
     * @param uniqueId the unique id to get the player
     * @return the player
     */
    @Nullable ICloudPlayer getCloudPlayerByUniqueIdOrNull(final @NotNull UUID uniqueId);

    /**
     * @param username the username to get the player
     * @return the player
     */
    @Nullable ICloudPlayer getCloudPlayerByNameOrNull(final @NotNull String username);

    /**
     * @return the online count
     */
    default int getCloudPlayerOnlineAmount() {
        return this.getAllCachedCloudPlayers().size();
    }

    /**
     * registers a cloud player
     * @param uniqueId the unique id of the player
     * @param username the username of the player
     */
    void registerCloudPlayer(final @NotNull UUID uniqueId, final @NotNull String username);

    /**
     * unregisters a cloud player
     * @param uniqueId the unique id of the player
     * @param username the username of the player
     */
    void unregisterCloudPlayer(final @NotNull UUID uniqueId, final @NotNull String username);

}
