package de.bytemc.cloud.plugin.bootstrap.proxy;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.cloud.plugin.IPlugin;
import de.bytemc.cloud.plugin.bootstrap.proxy.events.ProxyCloudEvents;
import de.bytemc.cloud.plugin.bootstrap.proxy.reconnect.ReconnectHandlerImpl;
import de.bytemc.cloud.plugin.events.proxy.ProxyEvents;
import de.bytemc.cloud.wrapper.Wrapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class ProxyBootstrap extends Plugin implements IPlugin {

    @Override
    public void onLoad() {
        ProxyServer.getInstance().setReconnectHandler(new ReconnectHandlerImpl());
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

        //update that the service is ready to use
        IService service = Wrapper.getInstance().thisService();
        service.setServiceVisibility(ServiceVisibility.VISIBLE);
        service.update();
    }

    @Override
    public void onDisable() {
        Wrapper.getInstance().thisService().edit(service -> {
            service.setServiceState(ServiceState.STOPPING);
            service.setServiceVisibility(ServiceVisibility.INVISIBLE);
        });
    }

    @Override
    public void shutdown() {
        this.getProxy().getScheduler().schedule(this, this.getProxy()::stop, 0, TimeUnit.MILLISECONDS);
    }


}
