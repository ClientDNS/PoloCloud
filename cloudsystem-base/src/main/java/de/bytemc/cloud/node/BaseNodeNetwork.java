package de.bytemc.cloud.node;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.CloudQueryPacket;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.network.NetworkManager;

public class BaseNodeNetwork {

    public BaseNodeNetwork() {
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(RedirectPacket.class, (ctx, packet) -> {
            Base.getInstance().getNode().getAllCachedConnectedClients().stream().filter(it -> it.getName().equalsIgnoreCase(packet.getNode())).forEach(it -> {
                it.sendPacket(packet.getPacket());
            });
        });

        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(CloudQueryPacket.class, (ctx, packet) -> {
            NetworkManager.callPacket(ctx, packet.getPacket());
            Base.getInstance().getNode().getAllCachedConnectedClients().stream().filter(it -> !it.getName().equalsIgnoreCase(packet.getIgnoredClients())).forEach(it -> {
                it.sendPacket(packet.getPacket());
            });
        });
    }

}
