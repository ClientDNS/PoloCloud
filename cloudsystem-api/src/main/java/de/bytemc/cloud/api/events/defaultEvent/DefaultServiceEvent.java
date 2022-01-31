package de.bytemc.cloud.api.events.defaultEvent;

import de.bytemc.cloud.api.events.ICloudEvent;
import de.bytemc.cloud.api.services.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public abstract class DefaultServiceEvent implements ICloudEvent {

    private IService service;

}
