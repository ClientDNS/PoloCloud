package de.polocloud.api.event.service;

import de.polocloud.api.event.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CloudServiceRemoveEvent implements CloudEvent {

    private String service;

}
