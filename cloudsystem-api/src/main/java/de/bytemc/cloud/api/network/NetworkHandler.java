package de.bytemc.cloud.api.network;

import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupExecutePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceShutdownPacket;
import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.IPacketListener;

public class NetworkHandler implements INetworkHandler {

    public NetworkHandler() {
        registerPackets(ServiceGroupExecutePacket.class, ServiceGroupCacheUpdatePacket.class, ServiceShutdownPacket.class, RedirectPacket.class);
    }

    @Override
    public void registerPacket(Class<? extends IPacket> packet) {
        NetworkManager.getRegisteredPacket().add(packet);
    }

    @Override
    public void registerPackets(Class<? extends IPacket>... packet) {
        for (Class<? extends IPacket> aClass : packet) registerPacket(aClass);
    }

    public <R extends IPacket> void registerPacketListener(Class<R> clazz, IPacketListener<R> packetListener){
        NetworkManager.registerPacketListener(clazz, packetListener);
    }

}
