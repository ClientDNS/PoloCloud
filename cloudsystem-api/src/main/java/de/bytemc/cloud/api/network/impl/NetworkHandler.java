package de.bytemc.cloud.api.network.impl;

import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupExecutePacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.network.packets.services.*;
import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.IPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class NetworkHandler implements INetworkHandler {

    public NetworkHandler() {

        System.out.println("amount of packets bevor: " + getAllCachedPackets().size());

        NetworkManager.registerPacket(ServiceGroupExecutePacket.class, 2);
        NetworkManager.registerPacket(ServiceGroupCacheUpdatePacket.class, 3);
        NetworkManager.registerPacket(ServiceShutdownPacket.class, 4);
        NetworkManager.registerPacket(RedirectPacket.class, 5);
        NetworkManager.registerPacket(CloudPlayerLoginPacket.class, 6);
        NetworkManager.registerPacket(CloudPlayerDisconnectPacket.class, 7);
        NetworkManager.registerPacket(ServiceAddPacket.class, 8);
        NetworkManager.registerPacket(ServiceRemovePacket.class, 9);
        NetworkManager.registerPacket(ServiceCacheUpdatePacket.class, 10);
        NetworkManager.registerPacket(ServiceStateUpdatePacket.class, 11);
        NetworkManager.registerPacket(ServiceRequestShutdownPacket.class, 12);

        System.out.println("amount of packets: " + getAllCachedPackets().size());

    }

    public <R extends IPacket> void registerPacketListener(@NotNull Class<R> clazz, @NotNull IPacketListener<R> packetListener){
        NetworkManager.registerPacketListener(clazz, packetListener);
    }

    @Override
    public Collection<Class<? extends IPacket>> getAllCachedPackets() {
        return NetworkManager.getRegisteredPackets().values();
    }

}
