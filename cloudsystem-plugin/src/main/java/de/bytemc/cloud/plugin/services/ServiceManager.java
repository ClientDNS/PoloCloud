package de.bytemc.cloud.plugin.services;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.services.*;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.AbstractSimpleServiceManager;
import de.bytemc.cloud.plugin.CloudPlugin;
import de.bytemc.cloud.plugin.services.file.PluginPropertyFileReader;
import de.bytemc.network.promise.ICommunicationPromise;

public class ServiceManager extends AbstractSimpleServiceManager {

    private final PluginPropertyFileReader property;

    public ServiceManager(PluginPropertyFileReader property) {
        this.property = property;
        INetworkHandler networkHandler = CloudAPI.getInstance().getNetworkHandler();

        networkHandler.registerPacketListener(ServiceShutdownPacket.class, (ctx, packet) -> CloudPlugin.getInstance().getPlugin().shutdown());
        networkHandler.registerPacketListener(ServiceCacheUpdatePacket.class, (ctx, packet) -> this.setAllCachedServices(packet.getAllCachedServices()));
        networkHandler.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) -> this.getAllCachedServices().remove(getServiceByNameOrNull(packet.getService())));
        networkHandler.registerPacketListener(ServiceAddPacket.class, (ctx, packet) -> this.getAllCachedServices().add(packet.getService()));
        networkHandler.registerPacketListener(ServiceStateUpdatePacket.class, (ctx, packet) -> getServiceByNameOrNull(packet.getService()).setServiceState(packet.getServiceState()));

    }

    @Override
    public ICommunicationPromise<IService> startService(IService service) {
        //TODO SEND PACKET
        return null;
    }

    public IService thisService() {
        return this.getAllCachedServices().stream().filter(it -> it.getName().equalsIgnoreCase(this.property.getService())).findAny().orElse(null);
    }

}
