package de.bytemc.cloud.api.events.events;

import de.bytemc.cloud.api.events.defaultEvent.DefaultServiceEvent;
import de.bytemc.cloud.api.services.IService;

public class CloudServiceUpdateEvent extends DefaultServiceEvent {

    public CloudServiceUpdateEvent(IService service) {
        super(service);
    }
}
