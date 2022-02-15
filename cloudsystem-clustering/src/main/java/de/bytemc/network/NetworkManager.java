package de.bytemc.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.IPacketListener;
import de.bytemc.network.packets.defaultpackets.HandshakeAuthenticationPacket;
import de.bytemc.network.packets.defaultpackets.NodeHandshakeAuthenticationPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NetworkManager {

    @Getter
    private static final Map<Integer, Class<? extends IPacket>> registeredPackets = Maps.newConcurrentMap();
    private static final Map<Class<? extends IPacket>, List<IPacketListener>> listeners = Maps.newConcurrentMap();

    static {
        registerPacket(HandshakeAuthenticationPacket.class, 0);
        registerPacket(NodeHandshakeAuthenticationPacket.class, 1);
    }

    public static <R extends IPacket> void registerPacketListener(Class<R> packetType, IPacketListener<R> packetListener) {
        List<IPacketListener> cache = NetworkManager.listeners.getOrDefault(packetType, new ArrayList<>());
        cache.add(packetListener);
        listeners.put(packetType, cache);
    }

    public static void callPacket(ChannelHandlerContext channelHandlerContext, IPacket packet) {
        listeners.getOrDefault(packet.getClass(), Lists.newArrayList()).forEach(it -> it.handle(channelHandlerContext, packet));
    }

    public static Optional<Integer> getPacketId(Class<? extends IPacket> clazz) {
        return registeredPackets.keySet().stream().filter(id -> registeredPackets.get(id).equals(clazz)).findAny();
    }

    public static void registerPacket(final @NotNull Class<? extends IPacket> packetClass, @NotNull final int id) {
        if (registeredPackets.containsKey(id)) {
            registerPacket(packetClass, (id + 1));
            return;
        }
        registeredPackets.put(id, packetClass);
    }

    public static int generatePacketId() {
        return registeredPackets.keySet().size() + 1;
    }

    public static Optional<Class<? extends IPacket>> getPacketClass(int id) {
        return registeredPackets.containsKey(id) ? Optional.of(registeredPackets.get(id)) : Optional.empty();
    }

}
