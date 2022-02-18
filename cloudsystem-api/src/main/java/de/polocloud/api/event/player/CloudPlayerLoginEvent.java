package de.polocloud.api.event.player;

import de.polocloud.api.player.CloudPlayer;
import org.jetbrains.annotations.NotNull;

public final class CloudPlayerLoginEvent extends DefaultPlayerEvent {

    public CloudPlayerLoginEvent(final @NotNull CloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

}
