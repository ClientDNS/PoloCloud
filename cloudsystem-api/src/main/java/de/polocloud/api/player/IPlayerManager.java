package de.polocloud.api.player;

import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPlayerManager {

    /**
     * @return a list of all cloud players
     */
    @NotNull List<ICloudPlayer> getPlayers();

    /**
     * @return a list of all cloud players on the service
     */
    @NotNull List<ICloudPlayer> getAllServicePlayers();

    /**
     * @param uniqueId the unique id to get the player
     * @return the player in an optional
     */
    @NotNull Optional<ICloudPlayer> getCloudPlayer(@NotNull UUID uniqueId);

    /**
     * @param username the username to get the player
     * @return the player in an optional
     */
    @NotNull Optional<ICloudPlayer> getCloudPlayer(@NotNull String username);

    /**
     * @param uniqueId the unique id to get the player
     * @return the player
     */
    @Nullable ICloudPlayer getCloudPlayerByUniqueIdOrNull(@NotNull UUID uniqueId);

    /**
     * @param username the username to get the player
     * @return the player
     */
    @Nullable ICloudPlayer getCloudPlayerByNameOrNull(@NotNull String username);

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
    void registerCloudPlayer(@NotNull ICloudPlayer cloudPlayer);

    /**
     * update a cloud player
     * @param cloudPlayer the unique id of the player
     */
    void updateCloudPlayer(@NotNull ICloudPlayer cloudPlayer);

    /**
     * update a cloud player
     * @param cloudPlayer the unique id of the player
     * @param updateReason the reason of the update
     */
    void updateCloudPlayer(@NotNull ICloudPlayer cloudPlayer, CloudPlayerUpdateEvent.@NotNull UpdateReason updateReason);

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
    void sendCloudPlayerMessage(@NotNull ICloudPlayer cloudPlayer, @NotNull String message);

}
