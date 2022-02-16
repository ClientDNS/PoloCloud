package de.polocloud.api.event.group;

import de.polocloud.api.event.ICloudEvent;
import de.polocloud.api.groups.IServiceGroup;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultServiceGroupEvent implements ICloudEvent {

    private final IServiceGroup serviceGroup;

    public DefaultServiceGroupEvent(final @NotNull IServiceGroup serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public IServiceGroup getServiceGroup() {
        return this.serviceGroup;
    }

}
