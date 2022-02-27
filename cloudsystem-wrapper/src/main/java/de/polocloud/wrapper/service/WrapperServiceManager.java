package de.polocloud.wrapper.service;

import de.polocloud.api.event.service.CloudServiceUpdateEvent;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.RedirectPacket;
import de.polocloud.api.network.packet.service.ServiceAddPacket;
import de.polocloud.api.network.packet.service.ServiceRemovePacket;
import de.polocloud.api.network.packet.service.ServiceUpdatePacket;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.service.ServiceManager;
import de.polocloud.network.packet.Packet;
import de.polocloud.wrapper.PropertyFile;
import de.polocloud.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class WrapperServiceManager implements ServiceManager {

    private List<CloudService> allCachedServices;
    private final PropertyFile property;

    private CloudService thisService;

    public WrapperServiceManager(final PropertyFile property) {
        this.allCachedServices = new CopyOnWriteArrayList<>();
        this.property = property;

        final var networkHandler = Wrapper.getInstance().getPacketHandler();

        networkHandler.registerPacketListener(ServiceUpdatePacket.class, (channelHandlerContext, packet) ->
            this.getService(packet.getService()).ifPresent(service -> {
                service.setState(packet.getState());
                service.setMaxPlayers(packet.getMaxPlayers());
                service.setMotd(packet.getMotd());
                Wrapper.getInstance().getEventHandler().call(new CloudServiceUpdateEvent(service));
            }));

        networkHandler.registerPacketListener(ServiceRemovePacket.class, (channelHandlerContext, packet) -> this.allCachedServices.remove(getServiceByNameOrNull(packet.getService())));
        networkHandler.registerPacketListener(ServiceAddPacket.class, (channelHandlerContext, packet) -> this.allCachedServices.add(packet.getService()));
    }

    @NotNull
    @Override
    public List<CloudService> getAllCachedServices() {
        return this.allCachedServices;
    }

    @Override
    public void setAllCachedServices(@NotNull List<CloudService> allCachedServices) {
        this.allCachedServices = allCachedServices;
        this.thisService = this.allCachedServices.stream()
            .filter(cloudService -> cloudService.getName().equalsIgnoreCase(this.property.getService())).findAny().orElse(null);
    }

    @Override
    public void startService(@NotNull CloudService service) {
        //TODO SEND PACKET
    }

    public CloudService thisService() {
        return this.thisService;
    }

    @Override
    public void updateService(@NotNull CloudService service) {
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new ServiceUpdatePacket(service), QueryPacket.QueryState.FIRST_RESPONSE));
    }

    @Override
    public void sendPacketToService(@NotNull CloudService service, @NotNull Packet packet) {
        if (service.equals(thisService())) {
            Wrapper.getInstance().getPacketHandler().call(null, packet);
            return;
        }
        Wrapper.getInstance().getClient().sendPacket(new RedirectPacket(service.getName(), packet));
    }

}
