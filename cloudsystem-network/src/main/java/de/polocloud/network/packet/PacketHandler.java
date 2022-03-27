package de.polocloud.network.packet;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class PacketHandler {

    private final List<Class<? extends Packet>> packets;
    @Getter private final Map<UUID, Consumer<Packet>> responses;

    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends Packet>, List<PacketListener>> packetListener;

    @SafeVarargs
    public PacketHandler(final Class<? extends Packet>... packets) {
        this.packets = Arrays.asList(packets);

        this.packetListener = new HashMap<>();
        this.responses = new HashMap<>();
    }

    public <T extends Packet> void registerPacketListener(final @NotNull Class<T> clazz, final @NotNull PacketListener<T> packetListener) {
        final var packetListeners = this.packetListener.getOrDefault(clazz, new ArrayList<>());
        packetListeners.add(packetListener);
        this.packetListener.put(clazz, packetListeners);
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void call(final ChannelHandlerContext channelHandlerContext, final @NotNull T t) {
        if (!this.packetListener.containsKey(t.getClass())) return;
        this.packetListener.get(t.getClass()).forEach(listener -> listener.handle(channelHandlerContext, t));
    }

    public int getPacketId(final Class<? extends Packet> clazz) {
        return this.packets.indexOf(clazz);
    }

    public Class<? extends Packet> getPacketClass(final int id) {
        return this.packets.get(id);
    }

}
