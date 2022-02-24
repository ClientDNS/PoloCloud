package de.polocloud.base.node;

import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.RedirectPacket;
import de.polocloud.api.network.packet.service.ServiceAddPacket;
import de.polocloud.api.network.packet.service.ServiceRemovePacket;
import de.polocloud.base.Base;
import de.polocloud.network.NetworkType;

public final class BaseNodeNetwork {

    public BaseNodeNetwork() {
        final var packetHandler = Base.getInstance().getPacketHandler();
        final var serviceManager = Base.getInstance().getServiceManager();

        packetHandler.registerPacketListener(QueryPacket.class, (channelHandlerContext, packet) -> {
            final var connectedClient = Base.getInstance().getNode().getClient(channelHandlerContext.channel());

            //send to all services as not query packet
            Base.getInstance().getNode().getServices().stream()
                .filter(it -> !it.equals(connectedClient)).forEach(it -> it.sendPacket(packet.getPacket()));

            if (packet.getState() == QueryPacket.QueryState.FIRST_RESPONSE) {
                //send to all another nodes
                Base.getInstance().getNode()
                    .sendPacketToType(new QueryPacket(packet.getPacket(), QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
            }
            //call local packet is communing
            packetHandler.call(channelHandlerContext, packet.getPacket());
        });

        packetHandler.registerPacketListener(RedirectPacket.class, (channelHandlerContext, packet) ->
             Base.getInstance().getServiceManager().getService(packet.getClient()).ifPresent(it -> {
                if (it.getGroup().getNode().equalsIgnoreCase(Base.getInstance().getNode().getName())) {
                    Base.getInstance().getNode().getClient(it.getName())
                        .ifPresent(service -> service.sendPacket(packet.getPacket()));
                } else {
                    Base.getInstance().getNode().getClient(it.getGroup().getNode())
                        .ifPresent(node -> node.sendPacket(new RedirectPacket(packet.getClient(), packet)));
                }
            }));

        packetHandler.registerPacketListener(ServiceRemovePacket.class, (channelHandlerContext, packet) ->
            serviceManager.getAllCachedServices().remove(serviceManager.getServiceByNameOrNull(packet.getService())));

        packetHandler.registerPacketListener(ServiceAddPacket.class, (ctx, packet) ->
            serviceManager.getAllCachedServices().add(packet.getService()));

    }

}
