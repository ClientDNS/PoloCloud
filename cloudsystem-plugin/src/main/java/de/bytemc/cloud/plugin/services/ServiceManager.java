package de.bytemc.cloud.plugin.services;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceShutdownPacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.AbstractSimpleServiceManager;
import de.bytemc.cloud.plugin.CloudPlugin;
import de.bytemc.cloud.plugin.services.file.PluginPropertyFileReader;
import de.bytemc.network.promise.ICommunicationPromise;

public class ServiceManager extends AbstractSimpleServiceManager {

    private final PluginPropertyFileReader property;

    public ServiceManager(PluginPropertyFileReader property) {
        this.property = property;
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceShutdownPacket.class, (ctx, packet) ->
            CloudPlugin.getInstance().getPlugin().shutdown());
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceCacheUpdatePacket.class, (ctx, packet) ->
            this.setAllCachedServices(packet.getAllCachedServices()));
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceRemovePacket.class, (ctx, packet) -> {
            this.getAllCachedServices().remove(getServiceByNameOrNull(packet.getService()));
            CloudAPI.getInstance().getLoggerProvider().logMessage("Service remove: " + packet.getService());
        });
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceAddPacket.class, (ctx, packet) -> {
            this.getAllCachedServices().add(packet.getService());

            //register proxy service
            CloudAPI.getInstance().getLoggerProvider().logMessage("Service add: " + packet.getService().getName());
        });
    }

    @Override
    public ICommunicationPromise<IService> startService(IService service) {
        //TODO SEND PACKET
        return null;
    }

    public IService thisService() {
        return this.getAllCachedServices().stream()
            .filter(it -> it.getName().equalsIgnoreCase(this.property.getService())).findAny().orElse(null);
    }

}
