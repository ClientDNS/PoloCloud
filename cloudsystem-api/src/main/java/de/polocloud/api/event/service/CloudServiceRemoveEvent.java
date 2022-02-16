package de.polocloud.api.event.service;

import de.polocloud.api.event.ICloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class CloudServiceRemoveEvent implements ICloudEvent {

    private String service;

}
