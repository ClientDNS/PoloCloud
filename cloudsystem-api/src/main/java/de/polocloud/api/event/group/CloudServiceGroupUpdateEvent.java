package de.polocloud.api.event.group;

import de.polocloud.api.groups.IServiceGroup;
import org.jetbrains.annotations.NotNull;

public class CloudServiceGroupUpdateEvent extends DefaultServiceGroupEvent {

    public CloudServiceGroupUpdateEvent(final @NotNull IServiceGroup serviceGroup) {
        super(serviceGroup);
    }

}
