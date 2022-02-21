package de.polocloud.base.service;

import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.base.Base;
import de.polocloud.api.network.packet.service.ServiceRequestShutdownPacket;
import de.polocloud.api.network.packet.service.ServiceUpdatePacket;
import de.polocloud.api.service.CloudService;
import de.polocloud.network.NetworkType;
import de.polocloud.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarInputStream;

public final class SimpleServiceManager implements de.polocloud.api.service.ServiceManager {

    private List<CloudService> allCachedServices;

    private final Path wrapperPath;

    private String wrapperMainClass;

    public SimpleServiceManager(final Path wrapperPath) {
        this.allCachedServices = new CopyOnWriteArrayList<>();

        this.wrapperPath = wrapperPath.toAbsolutePath();

        try (final JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(wrapperPath))) {
            this.wrapperMainClass = jarInputStream.getManifest().getMainAttributes().getValue("Main-Class");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Base.getInstance().getPacketHandler().registerPacketListener(ServiceUpdatePacket.class, (channelHandlerContext, packet) ->
            this.getService(packet.getService()).ifPresent(service -> {
                service.setServiceState(packet.getState());
                service.setServiceVisibility(packet.getServiceVisibility());
                service.setMaxPlayers(packet.getMaxPlayers());
                service.setMotd(packet.getMotd());
            }));

        Base.getInstance().getPacketHandler().registerPacketListener(ServiceRequestShutdownPacket.class,
            (channelHandlerContext, packet) ->
                Objects.requireNonNull(Base.getInstance().getServiceManager()
                    .getServiceByNameOrNull(packet.getService())).stop());
    }

    @NotNull
    @Override
    public List<CloudService> getAllCachedServices() {
        return this.allCachedServices;
    }

    @Override
    public void setAllCachedServices(@NotNull List<CloudService> services) {
        this.allCachedServices = services;
    }

    public void start(final CloudService service) {
        this.startService(service);
        Base.getInstance().getLogger().log("The service '§b" + service.getName() + "§7' selected and will now started.");
    }

    public void startService(final @NotNull CloudService service) {
        ((LocalService) service).start();
    }

    public void sendPacketToService(final @NotNull CloudService service, final @NotNull Packet packet) {
        Base.getInstance().getNode().getClients().stream()
            .filter(it -> it.name().equals(service.getName())).findAny().ifPresent(it -> it.sendPacket(packet));
    }

    @Override
    public void updateService(@NotNull CloudService service) {
        ServiceUpdatePacket packet = new ServiceUpdatePacket(service);
        //update all other nodes and this connected services
        Base.getInstance().getNode().sendPacketToType(new QueryPacket(packet, QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
        //update own service caches
        Base.getInstance().getNode().sendPacketToType(packet, NetworkType.WRAPPER);
    }

    public Path getWrapperPath() {
        return this.wrapperPath;
    }

    public String getWrapperMainClass() {
        return this.wrapperMainClass;
    }

}
