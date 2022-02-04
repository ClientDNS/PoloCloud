package de.bytemc.cloud.plugin.bootstrap.proxy;

import de.bytemc.cloud.plugin.IPlugin;
import de.bytemc.cloud.plugin.bootstrap.proxy.events.ProxyCloudEvents;
import de.bytemc.cloud.plugin.bootstrap.proxy.reconnect.ReconnectHandlerImpl;
import de.bytemc.cloud.plugin.events.proxy.ProxyEvents;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class ProxyBootstrap extends Plugin implements IPlugin {

    @Override
    public void onLoad() {
        ProxyServer.getInstance().setReconnectHandler(new ReconnectHandlerImpl());

        new ProxyCloudEvents();
    }

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getConfigurationAdapter().getServers().clear();
        ProxyServer.getInstance().getServers().clear();

        for (ListenerInfo listener : ProxyServer.getInstance().getConfigurationAdapter().getListeners()) {
            listener.getServerPriority().clear();
        }

        new ProxyCloudEvents();
        this.getProxy().getPluginManager().registerListener(this, new ProxyEvents());
    }

    @Override
    public void shutdown() {
        this.getProxy().getScheduler().schedule(this, this.getProxy()::stop, 0, TimeUnit.MILLISECONDS);
    }


}
