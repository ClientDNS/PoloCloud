package de.bytemc.cloud.api.network.impl;

import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.CloudQueryPacket;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupExecutePacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceShutdownPacket;
import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.IPacketListener;

public class NetworkHandler implements INetworkHandler {

    public NetworkHandler() {
        registerPackets(
            ServiceGroupExecutePacket.class,
            ServiceGroupCacheUpdatePacket.class,
            ServiceShutdownPacket.class,
            RedirectPacket.class,
            ServiceCacheUpdatePacket.class,
            CloudQueryPacket.class,
            CloudPlayerLoginPacket.class,
            CloudPlayerDisconnectPacket.class
        );

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
