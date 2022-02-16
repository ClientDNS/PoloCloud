package de.bytemc.cloud.plugin.bootstrap.velocity.listener;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.IEventHandler;
import de.bytemc.cloud.api.events.events.CloudServiceRegisterEvent;
import de.bytemc.cloud.api.events.events.CloudServiceRemoveEvent;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerKickPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerMessagePacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerSendServicePacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.network.NetworkManager;
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

        NetworkManager.registerPacketListener(CloudPlayerSendServicePacket.class, (ctx, packet) -> {
            proxyServer.getPlayer(packet.getUuid()).ifPresent(player -> {
                if (player.getCurrentServer().isEmpty() && player.getCurrentServer().get().getServerInfo().getName()
                    .equals(packet.getService()))
                    return;
                proxyServer.getServer(packet.getService()).ifPresent(registeredServer ->
                    player.createConnectionRequest(registeredServer).fireAndForget());
            });
        });

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
