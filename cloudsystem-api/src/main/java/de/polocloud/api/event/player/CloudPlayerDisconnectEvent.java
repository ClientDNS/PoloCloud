package de.polocloud.api.event.player;

import de.polocloud.api.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;

public final class CloudPlayerDisconnectEvent extends DefaultPlayerEvent {

    public CloudPlayerDisconnectEvent(final @NotNull ICloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

}
