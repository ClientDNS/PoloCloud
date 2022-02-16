package de.polocloud.api.event.player;

import de.polocloud.api.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;

public final class CloudPlayerUpdateEvent extends DefaultPlayerEvent {

    private final UpdateReason updateReason;

    public CloudPlayerUpdateEvent(final @NotNull ICloudPlayer cloudPlayer, final @NotNull UpdateReason updateReason) {
        super(cloudPlayer);
        this.updateReason = updateReason;
    }

    public UpdateReason getUpdateReason() {
        return this.updateReason;
    }

    public enum UpdateReason {
        UNKNOWN, SERVER_SWITCH
    }

}
