package de.bytemc.cloud.api.events;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudServiceGroupUpdateEvent;
import de.bytemc.cloud.api.events.events.CloudServiceRegisterEvent;
import de.bytemc.cloud.api.events.events.CloudServiceRemoveEvent;
import de.bytemc.cloud.api.events.events.CloudServiceUpdateEvent;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceUpdatePacket;
import de.bytemc.network.NetworkManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class EventHandler implements IEventHandler {

    private final Map<Class<? extends ICloudEvent>, List<Consumer>> events = Maps.newConcurrentMap();

    public EventHandler() {
        //service register event
        NetworkManager.registerPacketListener(ServiceAddPacket.class, (ctx, packet) ->
            this.call(new CloudServiceRegisterEvent(packet.getService())));

        //service remove event
        NetworkManager.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) ->
            this.call(new CloudServiceRemoveEvent(packet.getService())));

        //service state update event
        NetworkManager.registerPacketListener(ServiceUpdatePacket.class, (ctx, packet) ->
            this.call(new CloudServiceUpdateEvent(CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(packet.getService()))));

        // service group update event
        NetworkManager.registerPacketListener(ServiceGroupUpdatePacket.class, (ctx, packet) ->
            this.call(new CloudServiceGroupUpdateEvent(Objects.requireNonNull(
                CloudAPI.getInstance().getGroupManager().getServiceGroupByNameOrNull(packet.getName())))));
    }

    public <T extends ICloudEvent> void registerEvent(@NotNull Class<T> clazz, @NotNull Consumer<T> event) {
        final List<Consumer> consumers = events.getOrDefault(clazz, Lists.newArrayList());
        consumers.add(event);
        this.events.put(clazz, consumers);
    }

    public <T extends ICloudEvent> void call(@NotNull T t) {
        this.events.getOrDefault(t.getClass(), Lists.newArrayList()).forEach(it -> it.accept(t));
    }

}
