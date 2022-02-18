package de.polocloud.api.event.player;

import de.polocloud.api.player.CloudPlayer;
import org.jetbrains.annotations.NotNull;

public final class CloudPlayerDisconnectEvent extends DefaultPlayerEvent {

    public CloudPlayerDisconnectEvent(final @NotNull CloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

}
