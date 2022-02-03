package de.bytemc.cloud.api.events.events;

import de.bytemc.cloud.api.events.defaultEvent.DefaultPlayerEvent;
import de.bytemc.cloud.api.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;

public class CloudPlayerDisconnectEvent extends DefaultPlayerEvent {

    public CloudPlayerDisconnectEvent(final @NotNull ICloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

}
