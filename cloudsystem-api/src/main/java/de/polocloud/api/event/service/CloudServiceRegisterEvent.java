package de.polocloud.api.event.service;

import de.polocloud.api.service.IService;
import org.jetbrains.annotations.NotNull;

public final class CloudServiceRegisterEvent extends DefaultServiceEvent {

    public CloudServiceRegisterEvent(final @NotNull IService service) {
        super(service);
    }

}
