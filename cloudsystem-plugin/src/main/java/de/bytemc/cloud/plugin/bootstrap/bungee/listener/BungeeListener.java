package de.bytemc.cloud.plugin.bootstrap.bungee.listener;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudPlayerUpdateEvent;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;
import de.bytemc.cloud.plugin.bootstrap.bungee.BungeeBootstrap;
import de.bytemc.cloud.wrapper.Wrapper;
import de.bytemc.cloud.wrapper.service.ServiceManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Objects;

public final class BungeeListener implements Listener {

    private final BungeeBootstrap bootstrap;
    private final ICloudPlayerManager playerManager;

    public BungeeListener(final BungeeBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.playerManager = CloudAPI.getInstance().getCloudPlayerManager();
    }

    @EventHandler
    public void handle(LoginEvent event) {
        final var connection = event.getConnection();

        this.playerManager.registerCloudPlayer(new SimpleCloudPlayer(connection.getUniqueId(), connection.getName(),
            ((ServiceManager) CloudAPI.getInstance().getServiceManager()).thisService()));
    }

    @EventHandler
    public void handle(final ServerConnectEvent event) {
        if (event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY
            || event.getReason() == ServerConnectEvent.Reason.LOBBY_FALLBACK) {
            System.out.println("server connect event");
            System.out.println(event.getReason());
            System.out.println(event.getRequest());
            this.playerManager.getCloudPlayer(event.getPlayer().getUniqueId()).ifPresent(cloudPlayer ->
                this.bootstrap.getFallback(event.getPlayer()).map(service -> ProxyServer.getInstance().getServerInfo(service.getName()))
                    .ifPresentOrElse(event::setTarget, () ->
                        event.getPlayer().disconnect(new TextComponent("§cEs konnte kein passender Fallback gefunden werden."))));
        }
    }

    @EventHandler
    public void handle(final ServerSwitchEvent event) {
        CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(event.getPlayer().getUniqueId())
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
        final ServerPing response = event.getResponse();
        final ServerPing.Players players = response.getPlayers();

        players.setMax(Wrapper.getInstance().thisService().getMaxPlayers());
        players.setOnline(this.playerManager.getCloudPlayerOnlineAmount());

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
