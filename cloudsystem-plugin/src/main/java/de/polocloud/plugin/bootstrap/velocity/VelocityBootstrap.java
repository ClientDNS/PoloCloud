package de.polocloud.plugin.bootstrap.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.api.service.utils.ServiceVisibility;
import de.polocloud.plugin.bootstrap.velocity.listener.VelocityCloudListener;
import de.polocloud.plugin.bootstrap.velocity.listener.VelocityListener;
import de.polocloud.wrapper.Wrapper;

import java.util.Comparator;
import java.util.Optional;

@Plugin(id = "polocloud", name = "PoloCloud", authors = "HttpMarco", version = "2.0.0")
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

        // update that the service is ready to use
        final CloudService service = Wrapper.getInstance().thisService();

        if (service.getGroup().isAutoUpdating()) {
            service.setServiceVisibility(ServiceVisibility.VISIBLE);
            service.update();
        }
    }

    @Subscribe
    public void handle(final ProxyShutdownEvent event) {
        Wrapper.getInstance().thisService().edit(service -> {
            service.setServiceState(ServiceState.STOPPING);
            service.setServiceVisibility(ServiceVisibility.INVISIBLE);
        });
    }

    public Optional<CloudService> getFallback(final Player player) {
        return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream()
            .filter(service -> service.getServiceState() == ServiceState.ONLINE)
            .filter(service -> service.getServiceVisibility() == ServiceVisibility.VISIBLE)
            .filter(service -> !service.getGroup().getGameServerVersion().isProxy())
            .filter(service -> service.getGroup().isFallbackGroup())
            .filter(service -> (player.getCurrentServer().isEmpty()
                || !player.getCurrentServer().get().getServerInfo().getName().equals(service.getName())))
            .min(Comparator.comparing(CloudService::getOnlineCount));
    }

}
