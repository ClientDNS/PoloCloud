package de.bytemc.cloud.plugin.bootstrap.bungee.listener;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.IEventHandler;
import de.bytemc.cloud.api.events.events.CloudServiceRegisterEvent;
import de.bytemc.cloud.api.events.events.CloudServiceRemoveEvent;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerKickPacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerMessagePacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerSendServicePacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.network.NetworkManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

public final class BungeeCloudListener {

    public BungeeCloudListener() {

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

        NetworkManager.registerPacketListener(CloudPlayerKickPacket.class, (ctx, packet) -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
            assert player != null;
            player.disconnect(new TextComponent(packet.getReason()));
        });

        NetworkManager.registerPacketListener(CloudPlayerMessagePacket.class, (ctx, packet) -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
            assert player != null;
            player.sendMessage(new TextComponent(packet.getMessage()));
        });

        NetworkManager.registerPacketListener(CloudPlayerSendServicePacket.class, (ctx, packet) -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getUuid());
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

    public void registerService(IService service) {
        this.registerService(service.getName(), new InetSocketAddress(service.getHostName(), service.getPort()));
    }

}
