package de.polocloud.plugin.bootstrap.bungee.listener;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.player.PlayerManager;
import de.polocloud.api.player.impl.SimpleCloudPlayer;
import de.polocloud.api.service.ServiceManager;
import de.polocloud.plugin.bootstrap.bungee.BungeeBootstrap;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.service.WrapperServiceManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Objects;

public final class BungeeListener implements Listener {

    private final BungeeBootstrap bootstrap;

    private final PlayerManager playerManager;
    private final ServiceManager serviceManager;

    public BungeeListener(final BungeeBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.playerManager = CloudAPI.getInstance().getPlayerManager();
        this.serviceManager = CloudAPI.getInstance().getServiceManager();
    }

    @EventHandler
    public void handle(final LoginEvent event) {
        if (playerManager.getOnlineCount() >= Wrapper.getInstance().thisService().getMaxPlayers()) {
            event.setCancelReason(new TextComponent("§cThis network has reached the maximum number of players."));
            event.setCancelled(true);
            return;
        }

        final var connection = event.getConnection();
        this.playerManager.registerCloudPlayer(new SimpleCloudPlayer(connection.getUniqueId(), connection.getName(),
            ((WrapperServiceManager) serviceManager).thisService()));
    }

    @EventHandler
    public void handle(final ServerConnectEvent event) {
        if (event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY
            || event.getReason() == ServerConnectEvent.Reason.LOBBY_FALLBACK) {
            this.playerManager.getCloudPlayer(event.getPlayer().getUniqueId()).ifPresent(cloudPlayer ->
                this.bootstrap.getFallback(event.getPlayer()).map(service -> ProxyServer.getInstance().getServerInfo(service.getName()))
                    .ifPresentOrElse(event::setTarget, () ->
                        event.getPlayer().disconnect(new TextComponent("§cNo fallback could be found."))));
        }
    }

    @EventHandler
    public void handle(final ServerSwitchEvent event) {
        playerManager.getCloudPlayer(event.getPlayer().getUniqueId())
            .ifPresent(cloudPlayer -> {
                cloudPlayer.setServer(Objects.requireNonNull(CloudAPI.getInstance().getServiceManager()
                    .getServiceByNameOrNull(event.getPlayer().getServer().getInfo().getName())));
                cloudPlayer.update(CloudPlayerUpdateEvent.UpdateReason.SERVER_SWITCH);
            });
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        this.playerManager.unregisterCloudPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void handle(ProxyPingEvent event) {
        final var response = event.getResponse();
        final var players = response.getPlayers();

        players.setMax(Wrapper.getInstance().thisService().getMaxPlayers());
        players.setOnline(this.playerManager.getOnlineCount());

        response.setPlayers(players);
        event.setResponse(response);
    }

    @EventHandler
    public void handle(final ServerKickEvent event) {
        this.bootstrap.getFallback(event.getPlayer())
            .ifPresent(service -> {
                event.setCancelled(true);
                event.setCancelServer(ProxyServer.getInstance().getServerInfo(service.getName()));
            });
    }

}
