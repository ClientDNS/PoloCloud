package de.polocloud.api.player;

import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerManager {

    /**
     * @return a list of all cloud players
     */
    @NotNull List<CloudPlayer> getPlayers();

    /**
     * @return a list of all cloud players on the service
     */
    @NotNull List<CloudPlayer> getAllServicePlayers();

    /**
     * @param uniqueId the unique id to get the player
     * @return the player in an optional
     */
    @NotNull Optional<CloudPlayer> getCloudPlayer(@NotNull UUID uniqueId);

    /**
     * @param username the username to get the player
     * @return the player in an optional
     */
    @NotNull Optional<CloudPlayer> getCloudPlayer(@NotNull String username);

    /**
     * @param uniqueId the unique id to get the player
     * @return the player
     */
    @Nullable CloudPlayer getCloudPlayerByUniqueIdOrNull(@NotNull UUID uniqueId);

    /**
     * @param username the username to get the player
     * @return the player
     */
    @Nullable CloudPlayer getCloudPlayerByNameOrNull(@NotNull String username);

    /**
     * @return the online count
     */
    default int getOnlineCount() {
        return this.getPlayers().size();
    }

    /**
     * registers a cloud player
     * @param cloudPlayer the player to register
     */
    void registerCloudPlayer(@NotNull CloudPlayer cloudPlayer);

    /**
     * update a cloud player
     * @param cloudPlayer the unique id of the player
     */
    void updateCloudPlayer(@NotNull CloudPlayer cloudPlayer);

    /**
     * update a cloud player
     * @param cloudPlayer the unique id of the player
     * @param updateReason the reason of the update
     */
    void updateCloudPlayer(@NotNull CloudPlayer cloudPlayer, CloudPlayerUpdateEvent.@NotNull UpdateReason updateReason);

    /**
     * unregisters a cloud player
     * @param uniqueId the unique id of the player
     */
    void unregisterCloudPlayer(@NotNull UUID uniqueId);

    /**
     * sens a message to a cloud player
     * @param cloudPlayer the cloud player
     * @param message the message to send
     */
    void sendCloudPlayerMessage(@NotNull CloudPlayer cloudPlayer, @NotNull String message);

}
