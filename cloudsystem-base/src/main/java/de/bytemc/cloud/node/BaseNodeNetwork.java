package de.bytemc.cloud.node;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.QueryPacket;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.network.NetworkManager;
import de.bytemc.network.cluster.types.NetworkType;
import de.bytemc.network.master.cache.IConnectedClient;

public final class BaseNodeNetwork {

    public BaseNodeNetwork() {

        final INetworkHandler networkHandler = CloudAPI.getInstance().getNetworkHandler();
        final IServiceManager serviceManager = CloudAPI.getInstance().getServiceManager();

        networkHandler.registerPacketListener(QueryPacket.class, (ctx, packet) -> {

            IConnectedClient connectedClient = Base.getInstance().getNode().getConnectedClientByChannel(ctx.channel());

            //send to all services as not query packet
            Base.getInstance().getNode().getAllServices().stream()
                .filter(it -> !it.equals(connectedClient)).forEach(it -> it.sendPacket(packet.getPacket()));

            if(packet.getState() == QueryPacket.QueryState.FIRST_RESPONSE) {
                //send to all another nodes
                Base.getInstance().getNode().sendPacketToType(new QueryPacket(packet.getPacket(), QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
            }
            //call local packet is communing
            NetworkManager.callPacket(ctx, packet.getPacket());
        });

        networkHandler.registerPacketListener(RedirectPacket.class, (ctx, packet) -> {
           CloudAPI.getInstance().getServiceManager().getService(packet.getClient()).ifPresent(it -> {
                if(it.getServiceGroup().getNode().equalsIgnoreCase(Base.getInstance().getNode().getNodeName())) {
                    Base.getInstance().getNode().getAllCachedConnectedClients()
                        .stream()
                        .filter(client -> it.getName().equalsIgnoreCase(client.getName()))
                        .findAny()
                        .ifPresent(service -> service.sendPacket(packet.getPacket()));
                } else {
                    //TODO CHECK OTHER NODES AND SEND TO REDIRECT
                }
           });
        });

        networkHandler.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) ->
            serviceManager.getAllCachedServices().remove(serviceManager.getServiceByNameOrNull(packet.getService())));
        networkHandler.registerPacketListener(ServiceAddPacket.class, (ctx, packet) ->
            serviceManager.getAllCachedServices().add(packet.getService()));

    }

}
