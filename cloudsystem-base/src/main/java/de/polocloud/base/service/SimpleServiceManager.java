package de.polocloud.base.service;

import de.polocloud.api.event.service.CloudServiceUpdateEvent;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.service.ServiceRequestShutdownPacket;
import de.polocloud.api.network.packet.service.ServiceUpdatePacket;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.service.ServiceManager;
import de.polocloud.base.Base;
import de.polocloud.network.NetworkType;
import de.polocloud.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarInputStream;

public final class SimpleServiceManager implements ServiceManager {

    private final Path wrapperPath;
    private final Path pluginPath;
    private List<CloudService> allCachedServices;
    private String wrapperMainClass;

    public SimpleServiceManager() {
        this.allCachedServices = new CopyOnWriteArrayList<>();

        final var storageDirectory = new File("storage/jars");
        this.wrapperPath = new File(storageDirectory, "wrapper.jar").toPath().toAbsolutePath();
        this.pluginPath = new File(storageDirectory, "plugin.jar").toPath();

        try {
            storageDirectory.mkdirs();

            // copy wrapper and plugin jar
            Files.copy(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("wrapper.jar")),
                this.wrapperPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("plugin.jar")),
                this.pluginPath, StandardCopyOption.REPLACE_EXISTING);

            // gets the main class from the wrapper
            try (final var jarInputStream = new JarInputStream(Files.newInputStream(this.wrapperPath))) {
                this.wrapperMainClass = jarInputStream.getManifest().getMainAttributes().getValue("Main-Class");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Base.getInstance().getPacketHandler().registerPacketListener(ServiceUpdatePacket.class, (channelHandlerContext, packet) ->
            this.getService(packet.getService()).ifPresent(service -> {
                service.setState(packet.getState());
                service.setMaxPlayers(packet.getMaxPlayers());
                service.setMotd(packet.getMotd());
                Base.getInstance().getEventHandler().call(new CloudServiceUpdateEvent(service));
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
        Base.getInstance().getLogger().log("§7The service '§b" + service.getName() + "§7' was selected and will now be started.");
    }

    public void startService(final @NotNull CloudService service) {
        ((LocalService) service).start();
    }

    public void sendPacketToService(final @NotNull CloudService service, final @NotNull Packet packet) {
        Base.getInstance().getNode().getClients().stream()
            .filter(it -> it.name().equals(service.getName())).findAny().ifPresent(it -> it.sendPacket(packet));
    }

    @Override
    public void shutdownService(@NotNull CloudService service) {
        service.stop();
    }

    @Override
    public void updateService(@NotNull CloudService service) {
        final var packet = new ServiceUpdatePacket(service);
        //update all other nodes and this connected services
        Base.getInstance().getNode().sendPacketToType(new QueryPacket(packet, QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
        //update own service caches
        Base.getInstance().getNode().sendPacketToType(packet, NetworkType.WRAPPER);
    }

    public Path getWrapperPath() {
        return this.wrapperPath;
    }

    public Path getPluginPath() {
        return this.pluginPath;
    }

    public String getWrapperMainClass() {
        return this.wrapperMainClass;
    }

}
