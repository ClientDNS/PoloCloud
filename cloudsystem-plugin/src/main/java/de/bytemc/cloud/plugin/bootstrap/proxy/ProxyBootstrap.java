package de.bytemc.cloud.plugin.bootstrap.proxy;

import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.plugin.CloudPlugin;
import de.bytemc.cloud.plugin.IPlugin;
import de.bytemc.cloud.plugin.console.DefaultProxyCommandSender;
import de.bytemc.cloud.plugin.events.proxy.ProxyEvents;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class ProxyBootstrap extends Plugin implements IPlugin {

    @Override
    public void onLoad() {
        CloudPlugin.setCommandSender(new DefaultProxyCommandSender());
        new CloudPlugin(this);
    }

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new ProxyEvents());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void shutdown() {
        getProxy().getScheduler().schedule(this, () -> getProxy().stop(), 0, TimeUnit.MILLISECONDS);
    }

    public void registerService(IService service){
        ProxyServer.getInstance().getServers().put(service.getName(), ProxyServer.getInstance().constructServerInfo(service.getName(), new InetSocketAddress(service.getHostName(), service.getPort()), "PoloCloud", false));
    }

    public void unregisterService(IService service){
        ProxyServer.getInstance().getServers().remove(service.getName());
    }

}
