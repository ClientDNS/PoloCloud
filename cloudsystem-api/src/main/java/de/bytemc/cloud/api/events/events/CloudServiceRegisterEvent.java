package de.bytemc.cloud.api.events.events;

import de.bytemc.cloud.api.events.defaultEvent.DefaultServiceEvent;
import de.bytemc.cloud.api.services.IService;
import org.jetbrains.annotations.NotNull;

public class CloudServiceRegisterEvent extends DefaultServiceEvent {

    public CloudServiceRegisterEvent(final @NotNull IService service) {
        super(service);
    }

}
