package de.bytemc.cloud.api.events.events;

import de.bytemc.cloud.api.events.defaultEvent.DefaultServiceGroupEvent;
import de.bytemc.cloud.api.groups.IServiceGroup;
import org.jetbrains.annotations.NotNull;

public class CloudServiceGroupUpdateEvent extends DefaultServiceGroupEvent {

    public CloudServiceGroupUpdateEvent(final @NotNull IServiceGroup serviceGroup) {
        super(serviceGroup);
    }

}
