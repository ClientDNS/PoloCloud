package de.bytemc.cloud.plugin.bootstrap.proxy;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.plugin.IPlugin;
import de.bytemc.cloud.plugin.bootstrap.proxy.commands.CloudProxyCommand;
import de.bytemc.cloud.plugin.bootstrap.proxy.events.ProxyCloudEvents;
import de.bytemc.cloud.plugin.bootstrap.proxy.reconnect.ReconnectHandlerImpl;
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

        new ProxyCloudEvents();

        NetworkManager.registerPacketListener(ServiceAddPacket.class, (ctx, packet) -> {
            if (!packet.getService().getServiceGroup().getGameServerVersion().isProxy())
                this.registerService(packet.getService());
        });
        NetworkManager.registerPacketListener(ServiceRemovePacket.class, (ctx, packet) -> this.unregisterService(packet.getService()));
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
        getProxy().getPluginManager().registerCommand(this, new CloudProxyCommand());
    }

    @Override
    public void onDisable() {

    }


    @Override
    public void shutdown() {
        this.getProxy().getScheduler().schedule(this, this.getProxy()::stop, 0, TimeUnit.MILLISECONDS);
    }

    public void registerService(IService service) {
        registerService(service.getName(), new InetSocketAddress(service.getHostName(), service.getPort()));
    }

    public void unregisterService(String name) {
        ProxyServer.getInstance().getServers().remove(name);
    }

    public void registerFallbackService() {
        registerService("fallback", new InetSocketAddress("127.0.0.1", 0));
    }


    private void registerService(String name, InetSocketAddress socketAddress) {
        var info = ProxyServer.getInstance().constructServerInfo(name, socketAddress, "A PoloCloud service", false);
        ProxyServer.getInstance().getServers().put(name, info);
    }

}
