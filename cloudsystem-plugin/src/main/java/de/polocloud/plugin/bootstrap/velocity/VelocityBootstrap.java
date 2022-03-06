package de.polocloud.plugin.bootstrap.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.service.ServiceState;
import de.polocloud.plugin.bootstrap.velocity.commands.VelocityCloudCommand;
import de.polocloud.plugin.bootstrap.velocity.listener.VelocityCloudListener;
import de.polocloud.plugin.bootstrap.velocity.listener.VelocityListener;
import de.polocloud.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;

@SuppressWarnings("ClassCanBeRecord")
@Plugin(id = "polocloud", name = "PoloCloud", authors = "HttpMarco", version = "2.2.0-SNAPSHOT")
public final class VelocityBootstrap {

    private final ProxyServer proxyServer;

    @Inject
    public VelocityBootstrap(final ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void handle(final ProxyInitializeEvent event) {
        new VelocityCloudListener(this.proxyServer);

        this.proxyServer.getEventManager().register(this, new VelocityListener(this, this.proxyServer));

        CommandMeta meta = this.proxyServer.getCommandManager().metaBuilder("cloud").build();
        this.proxyServer.getCommandManager().register(meta, new VelocityCloudCommand());

        // update that the service is ready to use
        final var service = Wrapper.getInstance().thisService();

        if (service.getGroup().isAutoUpdating()) {
            service.edit(cloudService -> cloudService.setState(ServiceState.ONLINE));
        }
    }

    public @NotNull Optional<CloudService> getFallback(final Player player) {
        return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream()
            .filter(service -> service.getState().equals(ServiceState.ONLINE))
            .filter(service -> !service.getGroup().getGameServerVersion().isProxy())
            .filter(service -> service.getGroup().isFallbackGroup())
            .filter(service -> (player.getCurrentServer().isEmpty()
                || !player.getCurrentServer().get().getServerInfo().getName().equals(service.getName())))
            .min(Comparator.comparing(CloudService::getOnlineCount));
    }

}
