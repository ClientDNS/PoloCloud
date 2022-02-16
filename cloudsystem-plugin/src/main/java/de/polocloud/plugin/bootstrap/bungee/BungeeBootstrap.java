package de.polocloud.plugin.bootstrap.bungee;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.service.IService;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.api.service.utils.ServiceVisibility;
import de.polocloud.plugin.bootstrap.bungee.listener.BungeeCloudListener;
import de.polocloud.plugin.bootstrap.bungee.listener.BungeeListener;
import de.polocloud.wrapper.Wrapper;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Comparator;
import java.util.Optional;

public class BungeeBootstrap extends Plugin {

    @Override
    public void onEnable() {
        new BungeeCloudListener();
        this.getProxy().getPluginManager().registerListener(this, new BungeeListener(this));

        // update that the service is ready to use
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
            .min(Comparator.comparing(IService::getOnlineCount));
    }

}
