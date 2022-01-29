package de.bytemc.cloud.api.network;

import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.IPacketListener;

public interface INetworkHandler {

    <R extends IPacket> void registerPacketListener(Class<R> clazz, IPacketListener<R> packetListener);

}
