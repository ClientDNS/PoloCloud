package de.bytemc.cloud.wrapper.service;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceShutdownPacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.AbstractSimpleServiceManager;
import de.bytemc.cloud.wrapper.PropertyFileReader;
import de.bytemc.network.promise.ICommunicationPromise;
import org.jetbrains.annotations.NotNull;

public class ServiceManager extends AbstractSimpleServiceManager {

    private final PropertyFileReader property;

    public ServiceManager(final PropertyFileReader property) {
        this.property = property;
        INetworkHandler networkHandler = CloudAPI.getInstance().getNetworkHandler();
        networkHandler.registerPacketListener(ServiceShutdownPacket.class, (ctx, packet) -> System.exit(0) /*TODO better*/);
        networkHandler.registerPacketListener(ServiceCacheUpdatePacket.class, (ctx, packet) -> this.setAllCachedServices(packet.getAllCachedServices()));
        networkHandler.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) -> this.getAllCachedServices().remove(getServiceByNameOrNull(packet.getService())));
        networkHandler.registerPacketListener(ServiceAddPacket.class, (ctx, packet) -> this.getAllCachedServices().add(packet.getService()));
    }

    @Override
    public ICommunicationPromise<IService> startService(@NotNull IService service) {
        //TODO SEND PACKET
        return null;
    }

    public IService thisService() {
        return this.getAllCachedServices().stream().filter(it -> it.getName().equalsIgnoreCase(this.property.getService())).findAny().orElse(null);
    }

}
