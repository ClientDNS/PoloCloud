package de.bytemc.cloud.node;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.QueryPacket;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.network.NetworkManager;
import de.bytemc.network.cluster.types.NetworkType;
import de.bytemc.network.master.cache.IConnectedClient;

public final class BaseNodeNetwork {

    public BaseNodeNetwork() {
        final INetworkHandler networkHandler = CloudAPI.getInstance().getNetworkHandler();

        networkHandler.registerPacketListener(QueryPacket.class, (channelHandlerContext, packet) -> {

            IConnectedClient connectedClient = Base.getInstance().getNode().getConnectedClientByChannel(channelHandlerContext.channel());

            //send to all services as not query packet
            Base.getInstance().getNode().getAllServices().stream()
                .filter(it -> !it.equals(connectedClient)).forEach(it -> it.sendPacket(packet.getPacket()));

            if(packet.getState() == QueryPacket.QueryState.FIRST_RESPONSE) {
                //send to all another nodes
                Base.getInstance().getNode().sendPacketToType(new QueryPacket(packet.getPacket(), QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
            }
            //call local packet is communing
            NetworkManager.callPacket(channelHandlerContext, packet.getPacket());
        });

        networkHandler.registerPacketListener(RedirectPacket.class, (ctx, packet) ->
            Base.getInstance().getNode().getAllCachedConnectedClients().stream().filter(it ->
                it.getName().equalsIgnoreCase(packet.getNode())).forEach(it -> it.sendPacket(packet.getPacket())));

        final IServiceManager serviceManager = CloudAPI.getInstance().getServiceManager();

        networkHandler.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) ->
            serviceManager.getAllCachedServices().remove(serviceManager.getServiceByNameOrNull(packet.getService())));
        networkHandler.registerPacketListener(ServiceAddPacket.class, (ctx, packet) ->
            serviceManager.getAllCachedServices().add(packet.getService()));

    }

}
