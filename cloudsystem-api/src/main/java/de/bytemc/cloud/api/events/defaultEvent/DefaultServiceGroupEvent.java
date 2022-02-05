package de.bytemc.cloud.api.events.defaultEvent;

import de.bytemc.cloud.api.events.ICloudEvent;
import de.bytemc.cloud.api.groups.IServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class DefaultServiceGroupEvent implements ICloudEvent {

    private IServiceGroup serviceGroup;

}
