package de.polocloud.plugin.bootstrap.velocity.listener;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.polocloud.api.event.service.CloudServiceRegisterEvent;
import de.polocloud.api.event.service.CloudServiceRemoveEvent;
import de.polocloud.api.network.packet.player.CloudPlayerKickPacket;
import de.polocloud.api.network.packet.player.CloudPlayerMessagePacket;
import de.polocloud.api.network.packet.player.CloudPlayerSendServicePacket;
import de.polocloud.api.service.CloudService;
import de.polocloud.wrapper.Wrapper;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;

public record VelocityCloudListener(ProxyServer proxyServer) {

    public VelocityCloudListener(final ProxyServer proxyServer) {
        this.proxyServer = proxyServer;

        // load all current groups
        for (final var allCachedService : Wrapper.getInstance().getServiceManager().getAllCachedServices()) {
            if (!allCachedService.getGroup().getGameServerVersion().isProxy()) registerService(allCachedService);
        }

        // register events
        final var packetHandler = Wrapper.getInstance().getPacketHandler();
        final var eventHandler = Wrapper.getInstance().getEventHandler();

        eventHandler.registerEvent(CloudServiceRegisterEvent.class, event -> {
            if (!event.getService().getGroup().getGameServerVersion().isProxy())
                registerService(event.getService());
        });

        eventHandler.registerEvent(CloudServiceRemoveEvent.class, event -> unregisterService(event.getService()));

        packetHandler.registerPacketListener(CloudPlayerKickPacket.class, (channelHandlerContext, packet) ->
            proxyServer.getPlayer(packet.getUuid()).ifPresent(player -> player.disconnect(Component.text(packet.getReason()))));

        packetHandler.registerPacketListener(CloudPlayerMessagePacket.class, (channelHandlerContext, packet) ->
            proxyServer.getPlayer(packet.getUuid()).ifPresent(player -> player.sendMessage(Component.text(packet.getMessage()))));

        packetHandler.registerPacketListener(CloudPlayerSendServicePacket.class, (channelHandlerContext, packet) -> proxyServer.getPlayer(packet.getUuid()).ifPresent(player -> {
            if (player.getCurrentServer().isEmpty() && player.getCurrentServer().get().getServerInfo().getName()
                .equals(packet.getService()))
                return;
            proxyServer.getServer(packet.getService()).ifPresent(registeredServer ->
                player.createConnectionRequest(registeredServer).fireAndForget());
        }));

    }

    private void registerService(final String name, final InetSocketAddress socketAddress) {
        this.proxyServer.registerServer(new ServerInfo(name, socketAddress));
    }

    public void unregisterService(final String name) {
        this.proxyServer.getServer(name)
            .ifPresent(registeredServer -> this.proxyServer.unregisterServer(registeredServer.getServerInfo()));
    }

    public void registerService(final CloudService service) {
        this.registerService(service.getName(), new InetSocketAddress(service.getHostName(), service.getPort()));
    }

}
