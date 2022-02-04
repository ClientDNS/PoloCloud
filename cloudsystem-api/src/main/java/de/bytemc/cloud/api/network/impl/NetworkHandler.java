package de.bytemc.cloud.api.network.impl;

import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.QueryPacket;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupExecutePacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.*;
import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.IPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class NetworkHandler implements INetworkHandler {

    public NetworkHandler() {

        //Service packets
        NetworkManager.registerPacket(ServiceShutdownPacket.class, 2);
        NetworkManager.registerPacket(ServiceAddPacket.class, 3);
        NetworkManager.registerPacket(ServiceRemovePacket.class, 4);
        NetworkManager.registerPacket(ServiceCacheUpdatePacket.class, 5);
        NetworkManager.registerPacket(ServiceUpdatePacket.class, 6);
        NetworkManager.registerPacket(ServiceRequestShutdownPacket.class, 7);

        //service group packets
        NetworkManager.registerPacket(ServiceGroupExecutePacket.class, 8);
        NetworkManager.registerPacket(ServiceGroupCacheUpdatePacket.class, 9);

        //cloud player packets
        NetworkManager.registerPacket(CloudPlayerLoginPacket.class, 10);
        NetworkManager.registerPacket(CloudPlayerDisconnectPacket.class, 11);
        NetworkManager.registerPacket(CloudPlayerUpdatePacket.class, 12);

        //util packets
        NetworkManager.registerPacket(QueryPacket.class, 13);
        NetworkManager.registerPacket(RedirectPacket.class, 14);
    }

    public <R extends IPacket> void registerPacketListener(@NotNull Class<R> clazz, @NotNull IPacketListener<R> packetListener){
        NetworkManager.registerPacketListener(clazz, packetListener);
    }

    @Override
    public Collection<Class<? extends IPacket>> getAllCachedPackets() {
        return NetworkManager.getRegisteredPackets().values();
    }

}
