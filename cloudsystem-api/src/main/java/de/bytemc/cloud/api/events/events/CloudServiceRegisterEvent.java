package de.bytemc.cloud.api.events.events;

import de.bytemc.cloud.api.events.defaultEvent.DefaultServiceEvent;
import de.bytemc.cloud.api.services.IService;

public class CloudServiceRegisterEvent extends DefaultServiceEvent {
    public CloudServiceRegisterEvent(IService service) {
        super(service);
    }
}
