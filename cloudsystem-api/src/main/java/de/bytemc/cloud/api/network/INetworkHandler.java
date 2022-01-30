package de.bytemc.cloud.api.network;

import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.IPacketListener;
import org.jetbrains.annotations.NotNull;

public interface INetworkHandler {

    /**
     * registers a packet listener
     * @param clazz the class of the packet
     * @param packetListener the packet listener to register
     */
    <R extends IPacket> void registerPacketListener(final @NotNull Class<R> clazz, final @NotNull IPacketListener<R> packetListener);

}
