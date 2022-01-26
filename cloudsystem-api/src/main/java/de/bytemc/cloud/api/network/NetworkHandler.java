package de.bytemc.cloud.api.network;

import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;

public class NetworkHandler implements INetworkHandler {

    public NetworkHandler() {

    }

    @Override
    public void registerPacket(Class<? extends IPacket> packet) {
        NetworkManager.getRegisteredPacket().add(packet);
    }

    @Override
    public void registerPackets(Class<? extends IPacket>... packet) {

    }
}
