package de.polocloud.plugin.bootstrap.velocity.listener;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.event.service.CloudServiceRegisterEvent;
import de.polocloud.api.event.service.CloudServiceRemoveEvent;
import de.polocloud.api.network.packet.player.CloudPlayerKickPacket;
import de.polocloud.api.network.packet.player.CloudPlayerMessagePacket;
import de.polocloud.api.network.packet.player.CloudPlayerSendServicePacket;
import de.polocloud.api.service.IService;
import de.polocloud.network.NetworkManager;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;

public record VelocityCloudListener(ProxyServer proxyServer) {

    public VelocityCloudListener(final ProxyServer proxyServer) {
        this.proxyServer = proxyServer;

        // load all current groups
        for (final IService allCachedService : CloudAPI.getInstance().getServiceManager().getAllCachedServices()) {
            if (!allCachedService.getGroup().getGameServerVersion().isProxy()) registerService(allCachedService);
        }

        // register events
        final IEventHandler eventHandler = CloudAPI.getInstance().getEventHandler();

        eventHandler.registerEvent(CloudServiceRegisterEvent.class, event -> {
            if (!event.getService().getGroup().getGameServerVersion().isProxy())
                registerService(event.getService());
        });

        eventHandler.registerEvent(CloudServiceRemoveEvent.class, event -> unregisterService(event.getService()));

        NetworkManager.registerPacketListener(CloudPlayerKickPacket.class, (ctx, packet) ->
            proxyServer.getPlayer(packet.getUuid()).ifPresent(player -> player.disconnect(Component.text(packet.getReason()))));

        NetworkManager.registerPacketListener(CloudPlayerMessagePacket.class, (ctx, packet) ->
            proxyServer.getPlayer(packet.getUuid()).ifPresent(player -> player.sendMessage(Component.text(packet.getMessage()))));

        NetworkManager.registerPacketListener(CloudPlayerSendServicePacket.class, (ctx, packet) -> proxyServer.getPlayer(packet.getUuid()).ifPresent(player -> {
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

    public void registerService(final IService service) {
        this.registerService(service.getName(), new InetSocketAddress(service.getHostName(), service.getPort()));
    }

}
