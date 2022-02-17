package de.polocloud.base.node;

import de.polocloud.base.Base;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.network.INetworkHandler;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.RedirectPacket;
import de.polocloud.api.network.packet.service.ServiceAddPacket;
import de.polocloud.api.network.packet.service.ServiceRemovePacket;
import de.polocloud.api.service.IServiceManager;
import de.polocloud.network.NetworkManager;
import de.polocloud.network.cluster.type.NetworkType;
import de.polocloud.network.master.cache.IConnectedClient;

public final class BaseNodeNetwork {

    public BaseNodeNetwork() {

        final INetworkHandler networkHandler = CloudAPI.getInstance().getNetworkHandler();
        final IServiceManager serviceManager = CloudAPI.getInstance().getServiceManager();

        networkHandler.registerPacketListener(QueryPacket.class, (ctx, packet) -> {
            final var connectedClient = Base.getInstance().getNode().getConnectedClientByChannel(ctx.channel());

            //send to all services as not query packet
            Base.getInstance().getNode().getAllServices().stream()
                .filter(it -> !it.equals(connectedClient)).forEach(it -> it.sendPacket(packet.getPacket()));

            if (packet.getState() == QueryPacket.QueryState.FIRST_RESPONSE) {
                //send to all another nodes
                Base.getInstance().getNode()
                    .sendPacketToType(new QueryPacket(packet.getPacket(), QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
            }
            //call local packet is communing
            NetworkManager.callPacket(ctx, packet.getPacket());
        });

        networkHandler.registerPacketListener(RedirectPacket.class, (ctx, packet) ->
            CloudAPI.getInstance().getServiceManager().getService(packet.getClient()).ifPresent(it -> {
                if (it.getGroup().getNode().equalsIgnoreCase(Base.getInstance().getNode().getNodeName())) {
                    Base.getInstance().getNode().getClient(it.getName())
                        .ifPresent(service -> service.sendPacket(packet.getPacket()));
                } else {
                    Base.getInstance().getNode().getClient(it.getGroup().getNode())
                        .ifPresent(node -> node.sendPacket(new RedirectPacket(packet.getClient(), packet)));
                }
            }));

        networkHandler.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) ->
            serviceManager.getAllCachedServices().remove(serviceManager.getServiceByNameOrNull(packet.getService())));
        networkHandler.registerPacketListener(ServiceAddPacket.class, (ctx, packet) ->
            serviceManager.getAllCachedServices().add(packet.getService()));

    }

}
