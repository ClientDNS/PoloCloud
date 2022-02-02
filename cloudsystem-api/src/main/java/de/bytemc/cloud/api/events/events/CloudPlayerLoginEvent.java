package de.bytemc.cloud.api.events.events;

import de.bytemc.cloud.api.events.defaultEvent.DefaultPlayerEvent;
import de.bytemc.cloud.api.player.ICloudPlayer;

public class CloudPlayerLoginEvent extends DefaultPlayerEvent {

    public CloudPlayerLoginEvent(ICloudPlayer cloudPlayer) {
        super(cloudPlayer);
    }

}
