package de.bytemc.cloud.plugin.bootstrap.proxy;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.plugin.IPlugin;
import de.bytemc.cloud.plugin.events.proxy.ProxyEvents;
import de.bytemc.network.NetworkManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProxyBootstrap extends Plugin implements IPlugin {

    @Override
    public void onLoad() {
        ProxyServer.getInstance().setReconnectHandler(new ReconnectHandlerImpl());

        NetworkManager.registerPacketListener(ServiceAddPacket.class, (ctx, packet) -> {
            if (!packet.getService().getServiceGroup().getGameServerVersion().isProxy())
                this.registerService(packet.getService());
        });
        NetworkManager.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) ->
            this.unregisterService(CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(packet.getService())));

    }

    @Override
    public void onEnable() {
        for (IService allCachedService : CloudAPI.getInstance().getServiceManager().getAllCachedServices()) {
            if (!allCachedService.getServiceGroup().getGameServerVersion().isProxy()) registerService(allCachedService);
        }

        ProxyServer.getInstance().getConfigurationAdapter().getServers().clear();
        ProxyServer.getInstance().getServers().clear();

        for (ListenerInfo listener : ProxyServer.getInstance().getConfigurationAdapter().getListeners()) {
            listener.getServerPriority().clear();
        }

        registerFallbackService();
        for (IService allCachedService : CloudAPI.getInstance().getServiceManager().getAllCachedServices()) {
            if (!allCachedService.getServiceGroup().getGameServerVersion().isProxy()) registerService(allCachedService);
        }

        this.getProxy().getPluginManager().registerListener(this, new ProxyEvents());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void shutdown() {
        this.getProxy().getScheduler().schedule(this, this.getProxy()::stop, 0, TimeUnit.MILLISECONDS);
    }

    public void registerService(IService service) {
        ProxyServer.getInstance().getServers().put(service.getName(), ProxyServer.getInstance()
            .constructServerInfo(service.getName(), new InetSocketAddress(service.getHostName(), service.getPort()), "PoloCloud", false));
    }

    public void unregisterService(IService service) {
        ProxyServer.getInstance().getServers().remove(service.getName());
    }

    public void registerFallbackService() {
        registerService("fallback", UUID.fromString("00000000-0000-0000-0000-000000000000"), new InetSocketAddress("127.0.0.1", 0));
    }


    private void registerService(String name, UUID uniqueId, InetSocketAddress socketAddress) {
        var info = ProxyServer.getInstance().constructServerInfo(name, socketAddress, uniqueId.toString(), false);
        ProxyServer.getInstance().getServers().put(name, info);
    }

}
