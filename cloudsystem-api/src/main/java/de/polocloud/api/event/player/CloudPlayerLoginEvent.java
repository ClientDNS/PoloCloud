package de.polocloud.api.event.player;

import de.polocloud.api.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;

public final class CloudPlayerLoginEvent extends DefaultPlayerEvent {

    public CloudPlayerLoginEvent(final @NotNull ICloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

}
