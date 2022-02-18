package de.polocloud.api.event.group;

import de.polocloud.api.event.ICloudEvent;
import de.polocloud.api.groups.ServiceGroup;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultServiceGroupEvent implements ICloudEvent {

    private final ServiceGroup serviceGroup;

    public DefaultServiceGroupEvent(final @NotNull ServiceGroup serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public ServiceGroup getServiceGroup() {
        return this.serviceGroup;
    }

}
