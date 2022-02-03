package de.bytemc.cloud.plugin.bootstrap.proxy.events;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudServiceRegisterEvent;
import de.bytemc.cloud.api.events.events.CloudServiceRemoveEvent;
import de.bytemc.cloud.api.events.events.CloudServiceUpdateEvent;

public final class ProxyCloudEvents {

    public ProxyCloudEvents() {
        CloudAPI.getInstance().getEventHandler().registerEvent(CloudServiceRegisterEvent.class, event -> {
            System.out.println("Service register event: " + event.getService());
        });

        CloudAPI.getInstance().getEventHandler().registerEvent(CloudServiceRemoveEvent.class, event -> {
            System.out.println("Service register remove: " + event.getService());
        });

        CloudAPI.getInstance().getEventHandler().registerEvent(CloudServiceUpdateEvent.class, event -> {
            System.out.println("Service update: " + event.getService().getName());
        });
    }

}
