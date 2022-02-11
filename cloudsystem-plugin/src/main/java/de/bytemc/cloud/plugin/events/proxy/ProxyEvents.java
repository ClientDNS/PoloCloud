package de.bytemc.cloud.plugin.events.proxy;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.fallback.FallbackHandler;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.cloud.wrapper.Wrapper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public final class ProxyEvents implements Listener {

    private final ICloudPlayerManager playerManager;

    public ProxyEvents() {
        this.playerManager = CloudAPI.getInstance().getCloudPlayerManager();
    }

    @EventHandler
    public void handle(PreLoginEvent event) {
        if (!FallbackHandler.isFallbackAvailable()) {
            event.setCancelReason(new TextComponent("§cEs konnte kein passender Fallback gefunden werden."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(LoginEvent event) {
        playerManager.registerCloudPlayer(event.getConnection().getUniqueId(), event.getConnection().getName());
    }

    @EventHandler
    public void handle(ServerConnectEvent event) {
        this.playerManager.getCloudPlayer(event.getPlayer().getUniqueId()).ifPresent(cloudPlayer -> {
            if (event.getTarget().getName().equalsIgnoreCase("fallback")) {
                FallbackHandler.getLobbyFallbackOrNull().ifPresentOrElse(it -> {
                    event.setTarget(ProxyServer.getInstance().getServerInfo(it.getName()));
                    cloudPlayer.setServer(it);
                    cloudPlayer.setProxyServer(Wrapper.getInstance().thisService());
                    cloudPlayer.update();
                }, () -> {
                    event.getPlayer().disconnect(new TextComponent("§cEs konnte kein passender Fallback gefunden werden."));
                });
            }
        });
    }

    @EventHandler
    public void handle(final ServerSwitchEvent event) {
        CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(event.getPlayer().getUniqueId())
            .ifPresent(cloudPlayer -> {
                cloudPlayer.setServer(Objects.requireNonNull(CloudAPI.getInstance().getServiceManager()
                    .getServiceByNameOrNull(event.getPlayer().getServer().getInfo().getName())));
                cloudPlayer.update();
        });
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        playerManager.unregisterCloudPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    @EventHandler
    public void handle(ProxyPingEvent event) {
        final ServerPing response = event.getResponse();
        final ServerPing.Players players = response.getPlayers();

        response.setPlayers(new ServerPing.Players(Wrapper.getInstance().thisService().getMaxPlayers(), playerManager.getCloudPlayerOnlineAmount(), players.getSample()));
        event.setResponse(response);
    }

    @EventHandler
    public void handle(final ServerKickEvent event) {
        this.getFallback(event.getPlayer()).ifPresent(serverInfo -> {
            event.setCancelled(true);
            event.setCancelServer(serverInfo);
        });
    }

    //TODO CHECK FALLBACKHANDLER DUPLICATED?
    private Optional<ServerInfo> getFallback(final ProxiedPlayer player) {
        return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream()
            .filter(service -> service.getServiceState() == ServiceState.ONLINE)
            .filter(service -> service.getServiceVisibility() == ServiceVisibility.VISIBLE)
            .filter(service -> !service.getServiceGroup().getGameServerVersion().isProxy())
            .filter(service -> service.getServiceGroup().isFallbackGroup())
            .filter(service -> (player.getServer() == null || !player.getServer().getInfo().getName().equals(service.getName())))
            .min(Comparator.comparing(IService::getOnlinePlayers))
            .map(service -> ProxyServer.getInstance().getServerInfo(service.getName()));
    }
}
