package de.bytemc.cloud.node;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.services.IServiceManager;

public class BaseNodeNetwork {

    public BaseNodeNetwork() {
        final INetworkHandler networkHandler = CloudAPI.getInstance().getNetworkHandler();

        networkHandler.registerPacketListener(RedirectPacket.class, (ctx, packet) ->
            Base.getInstance().getNode().getAllCachedConnectedClients().stream().filter(it ->
                it.getName().equalsIgnoreCase(packet.getNode())).forEach(it -> it.sendPacket(packet.getPacket())));


        final IServiceManager serviceManager = CloudAPI.getInstance().getServiceManager();
        networkHandler.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) ->
            serviceManager.getAllCachedServices().remove(serviceManager.getServiceByNameOrNull(packet.getService())));
        networkHandler.registerPacketListener(ServiceAddPacket.class, (ctx, packet) -> serviceManager.getAllCachedServices().add(packet.getService()));

    }

}
