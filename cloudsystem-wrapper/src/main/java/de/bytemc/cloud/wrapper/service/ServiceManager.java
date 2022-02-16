package de.bytemc.cloud.wrapper.service;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.packets.QueryPacket;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.services.*;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.wrapper.PropertyFile;
import de.bytemc.cloud.wrapper.Wrapper;
import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.promise.ICommunicationPromise;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ServiceManager implements IServiceManager {

    private List<IService> allCachedServices;
    private final PropertyFile property;

    public ServiceManager(final PropertyFile property) {
        this.allCachedServices = new CopyOnWriteArrayList<>();
        this.property = property;

        final INetworkHandler networkHandler = CloudAPI.getInstance().getNetworkHandler();

        networkHandler.registerPacketListener(ServiceUpdatePacket.class, (ctx, packet) ->
            this.getService(packet.getService()).ifPresent(service -> {
                service.setServiceState(packet.getState());
                service.setServiceVisibility(packet.getServiceVisibility());
                service.setMaxPlayers(packet.getMaxPlayers());
                service.setMotd(packet.getMotd());
            }));

        networkHandler.registerPacketListener(ServiceCacheUpdatePacket.class, (ctx, packet) -> this.allCachedServices = packet.getAllCachedServices());

        networkHandler.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) -> this.allCachedServices.remove(getServiceByNameOrNull(packet.getService())));
        networkHandler.registerPacketListener(ServiceAddPacket.class, (ctx, packet) -> this.allCachedServices.add(packet.getService()));
    }

    @NotNull
    @Override
    public List<IService> getAllCachedServices() {
        return this.allCachedServices;
    }

    @Override
    public ICommunicationPromise<IService> startService(@NotNull IService service) {
        //TODO SEND PACKET
        return null;
    }

    public IService thisService() {
        return this.allCachedServices.stream().filter(it -> it.getName().equalsIgnoreCase(this.property.getService())).findAny().orElse(null);
    }

    @Override
    public void updateService(@NotNull IService service) {
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new ServiceUpdatePacket(service), QueryPacket.QueryState.FIRST_RESPONSE));
    }

    @Override
    public void sendPacketToService(IService service, IPacket packet) {
        if (service.equals(thisService())) {
            NetworkManager.callPacket(null, packet);
            return;
        }
        Wrapper.getInstance().getClient().sendPacket(new RedirectPacket(service.getName(), packet));
    }

}
