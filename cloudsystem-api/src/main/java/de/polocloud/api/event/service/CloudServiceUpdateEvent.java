package de.polocloud.api.event.service;

import de.polocloud.api.service.CloudService;
import org.jetbrains.annotations.NotNull;

public final class CloudServiceUpdateEvent extends DefaultServiceEvent {

    public CloudServiceUpdateEvent(final @NotNull CloudService service) {
        super(service);
    }

}
