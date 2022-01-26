package de.bytemc.cloud.api.network;

import de.bytemc.network.packets.IPacket;

public interface INetworkHandler {

    void registerPacket(Class<? extends IPacket> packet);

    void registerPackets(Class<? extends IPacket>... packet);

}
