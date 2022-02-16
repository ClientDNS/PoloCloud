package de.bytemc.cloud.plugin.bootstrap.bungee;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.cloud.plugin.bootstrap.bungee.listener.BungeeCloudListener;
import de.bytemc.cloud.plugin.bootstrap.bungee.listener.BungeeListener;
import de.bytemc.cloud.plugin.bootstrap.bungee.reconnect.ReconnectHandlerImpl;
import de.bytemc.cloud.wrapper.Wrapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Comparator;
import java.util.Optional;

public class BungeeBootstrap extends Plugin {

    @Override
    public void onLoad() {
        ProxyServer.getInstance().setReconnectHandler(new ReconnectHandlerImpl(this));
    }

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getConfigurationAdapter().getServers().clear();
        ProxyServer.getInstance().getServers().clear();

        for (ListenerInfo listener : ProxyServer.getInstance().getConfigurationAdapter().getListeners()) {
            listener.getServerPriority().clear();
        }

        new BungeeCloudListener();
        this.getProxy().getPluginManager().registerListener(this, new BungeeListener(this));

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

    public Optional<IService> getFallback(final ProxiedPlayer player) {
        return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream()
            .filter(service -> service.getServiceState() == ServiceState.ONLINE)
            .filter(service -> service.getServiceVisibility() == ServiceVisibility.VISIBLE)
            .filter(service -> !service.getGroup().getGameServerVersion().isProxy())
            .filter(service -> service.getGroup().isFallbackGroup())
            .filter(service -> (player.getServer() == null || !player.getServer().getInfo().getName().equals(service.getName())))
            .min(Comparator.comparing(IService::getOnlinePlayers));
    }

}
