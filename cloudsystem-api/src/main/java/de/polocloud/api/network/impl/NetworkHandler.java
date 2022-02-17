package de.polocloud.api.network.impl;

import de.polocloud.api.network.packet.init.CacheInitPacket;
import de.polocloud.api.network.packet.player.*;
import de.polocloud.api.network.packet.service.*;
import de.polocloud.api.network.INetworkHandler;
import de.polocloud.api.network.packet.CustomPacket;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.RedirectPacket;
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
        NetworkManager.registerPacket(ServiceUpdatePacket.class, 5);
        NetworkManager.registerPacket(ServiceRequestShutdownPacket.class, 6);

        // service group packets
        NetworkManager.registerPacket(ServiceGroupExecutePacket.class, 7);
        NetworkManager.registerPacket(ServiceGroupUpdatePacket.class, 8);

        // cloud player packets
        NetworkManager.registerPacket(CloudPlayerLoginPacket.class, 9);
        NetworkManager.registerPacket(CloudPlayerDisconnectPacket.class, 10);
        NetworkManager.registerPacket(CloudPlayerUpdatePacket.class, 11);
        NetworkManager.registerPacket(CloudPlayerKickPacket.class, 12);
        NetworkManager.registerPacket(CloudPlayerSendServicePacket.class, 13);
        NetworkManager.registerPacket(CloudPlayerMessagePacket.class, 14);

        // util packets
        NetworkManager.registerPacket(QueryPacket.class, 15);
        NetworkManager.registerPacket(RedirectPacket.class, 16);

        NetworkManager.registerPacket(CustomPacket.class, 17);

        // cache init packet
        NetworkManager.registerPacket(CacheInitPacket.class, 18);
    }

    public <R extends IPacket> void registerPacketListener(@NotNull Class<R> clazz, @NotNull IPacketListener<R> packetListener){
        NetworkManager.registerPacketListener(clazz, packetListener);
    }

    @Override
    public @NotNull Collection<Class<? extends IPacket>> getAllCachedPackets() {
        return NetworkManager.getRegisteredPackets().values();
    }

}
