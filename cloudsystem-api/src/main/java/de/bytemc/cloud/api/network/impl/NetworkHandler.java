package de.bytemc.cloud.api.network.impl;

import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.IPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class NetworkHandler implements INetworkHandler {

    public NetworkHandler() {
        NetworkManager.registerPacketsByPackage("de.bytemc.cloud.api.network.packets");
    }

    public <R extends IPacket> void registerPacketListener(@NotNull Class<R> clazz, @NotNull IPacketListener<R> packetListener){
        NetworkManager.registerPacketListener(clazz, packetListener);
    }

    @Override
    public @NotNull Collection<Class<? extends IPacket>> getAllCachedPackets() {
        return NetworkManager.getRegisteredPackets().values();
    }

}
