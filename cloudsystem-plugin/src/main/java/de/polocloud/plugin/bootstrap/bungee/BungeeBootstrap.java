package de.polocloud.plugin.bootstrap.bungee;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.service.ServiceState;
import de.polocloud.plugin.bootstrap.bungee.commands.BungeeCloudCommand;
import de.polocloud.plugin.bootstrap.bungee.listener.BungeeCloudListener;
import de.polocloud.plugin.bootstrap.bungee.listener.BungeeListener;
import de.polocloud.wrapper.Wrapper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;

public final class BungeeBootstrap extends Plugin {

    @Override
    public void onEnable() {
        new BungeeCloudListener();
        this.getProxy().getPluginManager().registerListener(this, new BungeeListener(this));

        // update that the service is ready to use
        var service = Wrapper.getInstance().thisService();

        if (service.getGroup().isAutoUpdating()) {
            service.setState(ServiceState.ONLINE);
            service.update();
        }

        getProxy().getPluginManager().registerCommand(this, new BungeeCloudCommand());
    }

    public @NotNull Optional<CloudService> getFallback(final ProxiedPlayer player) {
        return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream()
            .filter(service -> service.getState().equals(ServiceState.ONLINE))
            .filter(service -> !service.getGroup().getGameServerVersion().isProxy())
            .filter(service -> service.getGroup().isFallbackGroup())
            .filter(service -> (player.getServer() == null || !player.getServer().getInfo().getName().equals(service.getName())))
            .min(Comparator.comparing(CloudService::getOnlineCount));
    }

}
