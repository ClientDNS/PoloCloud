package de.polocloud.api.network.impl;

import de.polocloud.api.network.packet.player.*;
import de.polocloud.api.network.packet.service.*;
import de.polocloud.api.network.INetworkHandler;
import de.polocloud.api.network.packet.CustomPacket;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.RedirectPacket;
import de.polocloud.api.network.packet.group.ServiceGroupCacheUpdatePacket;
import de.polocloud.api.network.packet.group.ServiceGroupExecutePacket;
import de.polocloud.api.network.packet.group.ServiceGroupUpdatePacket;
import de.polocloud.network.NetworkManager;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.IPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class NetworkHandler implements INetworkHandler {

    public NetworkHandler() {
        // Service packets
        NetworkManager.registerPacket(ServiceAddPacket.class, 3);
        NetworkManager.registerPacket(ServiceRemovePacket.class, 4);
        NetworkManager.registerPacket(ServiceCacheUpdatePacket.class, 5);
        NetworkManager.registerPacket(ServiceUpdatePacket.class, 6);
        NetworkManager.registerPacket(ServiceRequestShutdownPacket.class, 7);

        // service group packets
        NetworkManager.registerPacket(ServiceGroupExecutePacket.class, 8);
        NetworkManager.registerPacket(ServiceGroupCacheUpdatePacket.class, 9);
        NetworkManager.registerPacket(ServiceGroupUpdatePacket.class, 10);

        // cloud player packets
        NetworkManager.registerPacket(CloudPlayerLoginPacket.class, 11);
        NetworkManager.registerPacket(CloudPlayerDisconnectPacket.class, 12);
        NetworkManager.registerPacket(CloudPlayerUpdatePacket.class, 13);
        NetworkManager.registerPacket(CloudPlayerCachePacket.class, 14);
        NetworkManager.registerPacket(CloudPlayerKickPacket.class, 15);
        NetworkManager.registerPacket(CloudPlayerSendServicePacket.class, 16);
        NetworkManager.registerPacket(CloudPlayerMessagePacket.class, 17);

        // util packets
        NetworkManager.registerPacket(QueryPacket.class, 18);
        NetworkManager.registerPacket(RedirectPacket.class, 19);

        NetworkManager.registerPacket(CustomPacket.class, 20);
    }

    public <R extends IPacket> void registerPacketListener(@NotNull Class<R> clazz, @NotNull IPacketListener<R> packetListener){
        NetworkManager.registerPacketListener(clazz, packetListener);
    }

    @Override
    public @NotNull Collection<Class<? extends IPacket>> getAllCachedPackets() {
        return NetworkManager.getRegisteredPackets().values();
    }

}
