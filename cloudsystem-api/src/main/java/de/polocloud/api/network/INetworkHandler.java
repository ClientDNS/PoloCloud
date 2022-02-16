package de.polocloud.api.network;

import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.IPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface INetworkHandler {

    /**
     * registers a packet listener
     * @param clazz the class of the packet
     * @param packetListener the packet listener to register
     */
    <R extends IPacket> void registerPacketListener(final @NotNull Class<R> clazz, final @NotNull IPacketListener<R> packetListener);

    /**
     * @return a collection of all registered packets
     */
    @NotNull Collection<Class<? extends IPacket>> getAllCachedPackets();

}
