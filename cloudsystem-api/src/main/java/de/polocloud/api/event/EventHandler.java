package de.polocloud.api.event;

import de.polocloud.api.event.service.CloudServiceRemoveEvent;
import de.polocloud.api.network.packet.group.ServiceGroupUpdatePacket;
import de.polocloud.api.network.packet.service.ServiceAddPacket;
import de.polocloud.api.network.packet.service.ServiceRemovePacket;
import de.polocloud.api.network.packet.service.ServiceUpdatePacket;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.group.CloudServiceGroupUpdateEvent;
import de.polocloud.api.event.service.CloudServiceRegisterEvent;
import de.polocloud.api.event.service.CloudServiceUpdateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class EventHandler implements IEventHandler {

    private final Map<Class<? extends ICloudEvent>, List<Consumer>> events = new ConcurrentHashMap<>();

    public EventHandler() {
        final var packetHandler = CloudAPI.getInstance().getPacketHandler();

        // service register event
        packetHandler.registerPacketListener(ServiceAddPacket.class, (channelHandlerContext, packet) ->
            this.call(new CloudServiceRegisterEvent(packet.getService())));

        // service remove event
        packetHandler.registerPacketListener(ServiceRemovePacket.class, (channelHandlerContext, packet) ->
            this.call(new CloudServiceRemoveEvent(packet.getService())));

        // service state update event
        packetHandler.registerPacketListener(ServiceUpdatePacket.class, (channelHandlerContext, packet) ->
            CloudAPI.getInstance().getServiceManager().getService(packet.getService()).ifPresent(it -> this.call(new CloudServiceUpdateEvent(it))));

        // service group update event
        packetHandler.registerPacketListener(ServiceGroupUpdatePacket.class, (channelHandlerContext, packet) ->
            this.call(new CloudServiceGroupUpdateEvent(Objects.requireNonNull(
                CloudAPI.getInstance().getGroupManager().getServiceGroupByNameOrNull(packet.getName())))));
    }

    public <T extends ICloudEvent> void registerEvent(@NotNull Class<T> clazz, @NotNull Consumer<T> event) {
        final List<Consumer> consumers = events.getOrDefault(clazz, new ArrayList<>());
        consumers.add(event);
        this.events.put(clazz, consumers);
    }

    @SuppressWarnings("unchecked")
    public <T extends ICloudEvent> void call(@NotNull T t) {
        this.events.getOrDefault(t.getClass(), new ArrayList<>()).forEach(it -> it.accept(t));
    }

}
