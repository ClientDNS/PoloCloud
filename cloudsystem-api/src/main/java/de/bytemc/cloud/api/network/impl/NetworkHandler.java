package de.bytemc.cloud.api.network.impl;

import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupExecutePacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerDisconnectPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerLoginPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceShutdownPacket;
import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.IPacketListener;
import org.jetbrains.annotations.NotNull;

public class NetworkHandler implements INetworkHandler {

    public NetworkHandler() {
        NetworkManager.registerPacket(ServiceGroupExecutePacket.class, 2);
        NetworkManager.registerPacket(ServiceGroupCacheUpdatePacket.class, 3);
        NetworkManager.registerPacket(ServiceShutdownPacket.class, 4);
        NetworkManager.registerPacket(RedirectPacket.class, 5);
        NetworkManager.registerPacket(CloudPlayerLoginPacket.class, 6);
        NetworkManager.registerPacket(CloudPlayerDisconnectPacket.class, 7);
        NetworkManager.registerPacket(ServiceAddPacket.class, 8);
        NetworkManager.registerPacket(ServiceRemovePacket.class, 9);
        NetworkManager.registerPacket(ServiceCacheUpdatePacket.class, 10);
    }

    public <R extends IPacket> void registerPacketListener(@NotNull Class<R> clazz, @NotNull IPacketListener<R> packetListener){
        NetworkManager.registerPacketListener(clazz, packetListener);
    }

}
