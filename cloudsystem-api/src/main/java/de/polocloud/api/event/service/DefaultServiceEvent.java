package de.polocloud.api.event.service;

import de.polocloud.api.event.ICloudEvent;
import de.polocloud.api.service.CloudService;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultServiceEvent implements ICloudEvent {

    private final CloudService service;

    public DefaultServiceEvent(final @NotNull CloudService service) {
        this.service = service;
    }

    public CloudService getService() {
        return this.service;
    }

}
