package de.polocloud.api.event;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.service.CloudServiceRegisterEvent;
import de.polocloud.api.event.service.CloudServiceRemoveEvent;
import de.polocloud.api.network.packet.service.ServiceAddPacket;
import de.polocloud.api.network.packet.service.ServiceRemovePacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class SimpleEventHandler implements EventHandler {

    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends CloudEvent>, List<Consumer>> events = new ConcurrentHashMap<>();

    public SimpleEventHandler() {
        final var packetHandler = CloudAPI.getInstance().getPacketHandler();

        // service register event
        packetHandler.registerPacketListener(ServiceAddPacket.class, (channelHandlerContext, packet) ->
            this.call(new CloudServiceRegisterEvent(packet.getService())));

        // service remove event
        packetHandler.registerPacketListener(ServiceRemovePacket.class, (channelHandlerContext, packet) ->
            this.call(new CloudServiceRemoveEvent(packet.getService())));
    }

    public <T extends CloudEvent> void registerEvent(@NotNull Class<T> clazz, @NotNull Consumer<T> event) {
        final var consumers = this.events.getOrDefault(clazz, new ArrayList<>());
        consumers.add(event);
        this.events.put(clazz, consumers);
    }

    @SuppressWarnings("unchecked")
    public <T extends CloudEvent> void call(@NotNull T t) {
        this.events.getOrDefault(t.getClass(), new ArrayList<>()).forEach(it -> it.accept(t));
    }

}
