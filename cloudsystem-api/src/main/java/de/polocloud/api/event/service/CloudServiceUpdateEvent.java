package de.polocloud.api.event.service;

import de.polocloud.api.service.IService;
import org.jetbrains.annotations.NotNull;

public final class CloudServiceUpdateEvent extends DefaultServiceEvent {

    public CloudServiceUpdateEvent(final @NotNull IService service) {
        super(service);
    }

}
