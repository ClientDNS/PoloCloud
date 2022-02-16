package de.bytemc.cloud.plugin.bootstrap.bungee;

import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.cloud.plugin.bootstrap.bungee.listener.BungeeCloudListener;
import de.bytemc.cloud.plugin.bootstrap.bungee.listener.BungeeListener;
import de.bytemc.cloud.plugin.bootstrap.bungee.reconnect.ReconnectHandlerImpl;
import de.bytemc.cloud.wrapper.Wrapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeBootstrap extends Plugin {

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

        new BungeeCloudListener();
        this.getProxy().getPluginManager().registerListener(this, new BungeeListener());

        //update that the service is ready to use
        IService service = Wrapper.getInstance().thisService();

        if (service.getGroup().isAutoUpdating()) {
            service.setServiceVisibility(ServiceVisibility.VISIBLE);
            service.update();
        }
    }

    @Override
    public void onDisable() {
        Wrapper.getInstance().thisService().edit(service -> {
            service.setServiceState(ServiceState.STOPPING);
            service.setServiceVisibility(ServiceVisibility.INVISIBLE);
        });
    }

}
