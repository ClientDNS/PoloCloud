package de.bytemc.cloud.api.events.defaultEvent;

import de.bytemc.cloud.api.events.ICloudEvent;
import de.bytemc.cloud.api.player.ICloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public abstract class DefaultPlayerEvent implements ICloudEvent {

    private ICloudPlayer cloudPlayer;

}
