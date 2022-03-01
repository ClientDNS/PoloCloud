package de.polocloud.api.event.player;

import de.polocloud.api.player.CloudPlayer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public final class CloudPlayerUpdateEvent extends DefaultPlayerEvent {

    private final UpdateReason updateReason;

    public CloudPlayerUpdateEvent(final @NotNull CloudPlayer cloudPlayer, final @NotNull UpdateReason updateReason) {
        super(cloudPlayer);
        this.updateReason = updateReason;
    }

    public enum UpdateReason {
        UNKNOWN, SERVER_SWITCH
    }
}
