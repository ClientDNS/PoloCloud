package de.polocloud.api.event.service;

import de.polocloud.api.event.ICloudEvent;
import de.polocloud.api.service.IService;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultServiceEvent implements ICloudEvent {

    private final IService service;

    public DefaultServiceEvent(final @NotNull IService service) {
        this.service = service;
    }

    public IService getService() {
        return this.service;
    }

}
