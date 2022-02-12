package de.bytemc.cloud.api.events.events;

import de.bytemc.cloud.api.events.defaultEvent.DefaultPlayerEvent;
import de.bytemc.cloud.api.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;

public class CloudPlayerUpdateEvent extends DefaultPlayerEvent {

    private final UpdateReason updateReason;

    public CloudPlayerUpdateEvent(final @NotNull ICloudPlayer cloudPlayer, final @NotNull UpdateReason updateReason) {
        super(cloudPlayer);
        this.updateReason = updateReason;
    }

    public UpdateReason getUpdateReason() {
        return this.updateReason;
    }

    public enum UpdateReason {

        UNKNOWN,
        SERVER_SWITCH;

    }

}
