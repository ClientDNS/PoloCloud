package de.bytemc.cloud.api.events;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudServiceRegisterEvent;
import de.bytemc.cloud.api.events.events.CloudServiceRemoveEvent;
import de.bytemc.cloud.api.events.events.CloudServiceUpdateEvent;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceStateUpdatePacket;
import de.bytemc.network.NetworkManager;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventHandler implements IEventHandler {

    private final Map<Class<? extends ICloudEvent>, List<Consumer>> events = Maps.newConcurrentMap();

    public EventHandler() {
        //service register event
        NetworkManager.registerPacketListener(ServiceAddPacket.class, (ctx, packet) -> call(new CloudServiceRegisterEvent(packet.getService())));

        //service remove event
        NetworkManager.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) -> call(new CloudServiceRemoveEvent(packet.getService())));

        //service state update event
        NetworkManager.registerPacketListener(ServiceStateUpdatePacket.class, (ctx, packet) -> call(new CloudServiceUpdateEvent(CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(packet.getService()))));
    }

    public <T extends ICloudEvent> void registerEvent(Class<T> clazz, Consumer<T> event) {
        final List<Consumer> consumers = events.getOrDefault(clazz, Lists.newArrayList());
        consumers.add(event);
        this.events.put(clazz, consumers);
    }

    public <T extends ICloudEvent> void call(T t) {
        this.events.getOrDefault(t.getClass(), Lists.newArrayList()).forEach(it -> it.accept(t));
    }

}
