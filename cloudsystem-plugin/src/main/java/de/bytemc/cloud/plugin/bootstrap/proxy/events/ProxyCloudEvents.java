package de.bytemc.cloud.plugin.bootstrap.proxy.events;

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

public final class ProxyCloudEvents {

    public ProxyCloudEvents() {

        // register default fallback
        this.registerFallbackService();

        // load all current groups
        for (final IService allCachedService : CloudAPI.getInstance().getServiceManager().getAllCachedServices()) {
            if (!allCachedService.getServiceGroup().getGameServerVersion().isProxy()) registerService(allCachedService);
        }

        // register events
        final IEventHandler eventHandler = CloudAPI.getInstance().getEventHandler();

        eventHandler.registerEvent(CloudServiceRegisterEvent.class, event -> {
            if (!event.getService().getServiceGroup().getGameServerVersion().isProxy())
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
            if (player.getServer().getInfo().getName().equals(packet.getService())) return;
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

    private void registerFallbackService() {
        this.registerService("fallback", new InetSocketAddress("127.0.0.1", 0));
    }

}
