package de.polocloud.network.packet;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PacketHandler {

    private final List<Class<? extends Packet>> packets;

    private final Map<Class<? extends Packet>, List<PacketListener>> packetListener;

    @SafeVarargs
    public PacketHandler(final Class<? extends Packet>... packets) {
        this.packets = Arrays.asList(packets);

        this.packetListener = new ConcurrentHashMap<>();
    }

    public <T extends Packet> void registerPacketListener(final @NotNull Class<T> clazz, final @NotNull PacketListener<T> packetListener) {
        final List<PacketListener> packetListeners = this.packetListener.getOrDefault(clazz, new ArrayList<>());
        packetListeners.add(packetListener);
        this.packetListener.put(clazz, packetListeners);
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void call(final ChannelHandlerContext channelHandlerContext, final @NotNull T t) {
        this.packetListener.get(t.getClass()).forEach(listener -> listener.handle(channelHandlerContext, t));
    }

    public int getPacketId(final Class<? extends Packet> clazz) {
        return this.packets.indexOf(clazz);
    }

    public Class<? extends Packet> getPacketClass(final int id) {
        return this.packets.get(id);
    }

}
