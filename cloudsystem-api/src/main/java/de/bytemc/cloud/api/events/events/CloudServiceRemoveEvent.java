package de.bytemc.cloud.api.events.events;

import de.bytemc.cloud.api.events.ICloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CloudServiceRemoveEvent implements ICloudEvent {

    private String service;


}
