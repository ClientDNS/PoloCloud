package de.polocloud.wrapper.service;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.network.INetworkHandler;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.RedirectPacket;
import de.polocloud.api.network.packet.service.ServiceAddPacket;
import de.polocloud.api.network.packet.service.ServiceCacheUpdatePacket;
import de.polocloud.api.network.packet.service.ServiceRemovePacket;
import de.polocloud.api.network.packet.service.ServiceUpdatePacket;
import de.polocloud.api.service.IService;
import de.polocloud.api.service.IServiceManager;
import de.polocloud.wrapper.PropertyFile;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.network.NetworkManager;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.promise.ICommunicationPromise;
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