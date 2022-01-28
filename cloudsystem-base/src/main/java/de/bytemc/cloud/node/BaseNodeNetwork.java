package de.bytemc.cloud.node;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.CloudQueryPacket;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.network.NetworkManager;

public class BaseNodeNetwork {

    public BaseNodeNetwork() {
        INetworkHandler networkHandler = CloudAPI.getInstance().getNetworkHandler();

        networkHandler.registerPacketListener(RedirectPacket.class, (ctx, packet) ->
            Base.getInstance().getNode().getAllCachedConnectedClients().stream().filter(it ->
                it.getName().equalsIgnoreCase(packet.getNode())).forEach(it -> it.sendPacket(packet.getPacket())));

        networkHandler.registerPacketListener(CloudQueryPacket.class, (ctx, packet) -> {
            NetworkManager.callPacket(ctx, packet.getPacket());
            Base.getInstance().getNode().getAllCachedConnectedClients().stream().filter(it ->
                !it.getName().equalsIgnoreCase(packet.getIgnoredClients())).forEach(it ->
                it.sendPacket(packet.getPacket()));
        });

        IServiceManager serviceManager = CloudAPI.getInstance().getServiceManager();
        networkHandler.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) ->
            serviceManager.getAllCachedServices().remove( serviceManager.getServiceByNameOrNull(packet.getService())));
        networkHandler.registerPacketListener(ServiceAddPacket.class, (ctx, packet) -> serviceManager.getAllCachedServices().add(packet.getService()));

    }

}
