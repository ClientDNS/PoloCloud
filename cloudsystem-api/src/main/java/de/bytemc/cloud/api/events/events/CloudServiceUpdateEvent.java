package de.bytemc.cloud.api.events.events;

import de.bytemc.cloud.api.events.defaultEvent.DefaultServiceEvent;
import de.bytemc.cloud.api.services.IService;
import org.jetbrains.annotations.NotNull;

public class CloudServiceUpdateEvent extends DefaultServiceEvent {

    public CloudServiceUpdateEvent(final @NotNull IService service) {
        super(service);
    }

}
