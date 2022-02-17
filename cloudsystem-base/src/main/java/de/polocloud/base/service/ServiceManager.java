package de.polocloud.base.service;

import de.polocloud.base.Base;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.service.ServiceRequestShutdownPacket;
import de.polocloud.api.network.packet.service.ServiceUpdatePacket;
import de.polocloud.api.service.IService;
import de.polocloud.api.service.IServiceManager;
import de.polocloud.network.cluster.type.NetworkType;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.promise.ICommunicationPromise;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ServiceManager implements IServiceManager {

    private List<IService> allCachedServices;

    public ServiceManager() {
        this.allCachedServices = new CopyOnWriteArrayList<>();

        Base.getInstance().getNetworkHandler().registerPacketListener(ServiceUpdatePacket.class, (ctx, packet) ->
            this.getService(packet.getService()).ifPresent(service -> {
                service.setServiceState(packet.getState());
                service.setServiceVisibility(packet.getServiceVisibility());
                service.setMaxPlayers(packet.getMaxPlayers());
                service.setMotd(packet.getMotd());
            }));

        Base.getInstance().getNetworkHandler().registerPacketListener(ServiceRequestShutdownPacket.class,
            (channelHandlerContext, serviceRequestShutdownPacket) ->
                Objects.requireNonNull(Base.getInstance().getServiceManager()
                    .getServiceByNameOrNull(serviceRequestShutdownPacket.getService())).stop());
    }

    @NotNull
    @Override
    public List<IService> getAllCachedServices() {
        return this.allCachedServices;
    }

    @Override
    public void setAllCachedServices(@NotNull List<IService> services) {
        this.allCachedServices = services;
    }

    public void start(final IService service) {
        this.startService(service).addResultListener(it ->
            Base.getInstance().getLogger()
                .log("The service '§b" + service.getName() + "§7' selected and will now started."))
            .addFailureListener(Throwable::printStackTrace);
    }

    public ICommunicationPromise<IService> startService(final @NotNull IService service) {
        return ((LocalService) service).start();
    }

    public void sendPacketToService(final IService service, final IPacket packet) {
        Base.getInstance().getNode().getAllCachedConnectedClients().stream()
            .filter(it -> it.getName().equals(service.getName())).findAny().ifPresent(it -> it.sendPacket(packet));
    }

    @Override
    public void updateService(@NotNull IService service) {
        ServiceUpdatePacket packet = new ServiceUpdatePacket(service);
        //update all other nodes and this connected services
        Base.getInstance().getNode().sendPacketToType(new QueryPacket(packet, QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
        //update own service caches
        Base.getInstance().getNode().sendPacketToType(packet, NetworkType.SERVICE);
    }

}
