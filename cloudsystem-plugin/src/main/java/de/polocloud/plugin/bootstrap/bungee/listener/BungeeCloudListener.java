package de.polocloud.plugin.bootstrap.bungee.listener;

import de.polocloud.api.event.service.CloudServiceRegisterEvent;
import de.polocloud.api.event.service.CloudServiceRemoveEvent;
import de.polocloud.api.network.packet.player.CloudPlayerKickPacket;
import de.polocloud.api.network.packet.player.CloudPlayerMessagePacket;
import de.polocloud.api.network.packet.player.CloudPlayerSendServicePacket;
import de.polocloud.api.service.CloudService;
import de.polocloud.wrapper.Wrapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

public final class BungeeCloudListener {

    public BungeeCloudListener() {

        // load all current groups
        for (final CloudService allCachedService : Wrapper.getInstance().getServiceManager().getAllCachedServices()) {
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

        packetHandler.registerPacketListener(CloudPlayerKickPacket.class, (channelHandlerContext, packet) -> {
            var player = ProxyServer.getInstance().getPlayer(packet.getUuid());
            assert player != null;
            player.disconnect(new TextComponent(packet.getReason()));
        });

        packetHandler.registerPacketListener(CloudPlayerMessagePacket.class, (channelHandlerContext, packet) -> {
            var player = ProxyServer.getInstance().getPlayer(packet.getUuid());
            assert player != null;
            player.sendMessage(new TextComponent(packet.getMessage()));
        });

        packetHandler.registerPacketListener(CloudPlayerSendServicePacket.class, (channelHandlerContext, packet) -> {
            var player = ProxyServer.getInstance().getPlayer(packet.getUuid());
            assert player != null;
            if (player.getServer() != null && player.getServer().getInfo().getName().equals(packet.getService())) return;
            player.connect(ProxyServer.getInstance().getServerInfo(packet.getService()));
        });
    }

    private void registerService(String name, InetSocketAddress socketAddress) {
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, socketAddress, "Service", false));
    }

    public void unregisterService(String name) {
        ProxyServer.getInstance().getServers().remove(name);
    }

    public void registerService(CloudService service) {
        this.registerService(service.getName(), new InetSocketAddress(service.getHostName(), service.getPort()));
    }

}
