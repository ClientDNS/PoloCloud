package de.bytemc.cloud.plugin.bootstrap.bungee.listener;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudPlayerUpdateEvent;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.cloud.wrapper.Wrapper;
import de.bytemc.cloud.wrapper.service.ServiceManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public final class BungeeListener implements Listener {

    private final ICloudPlayerManager playerManager;

    public BungeeListener() {
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
        if (event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) return;
        this.playerManager.getCloudPlayer(event.getPlayer().getUniqueId()).ifPresent(cloudPlayer ->
            this.getFallback(event.getPlayer()).map(service -> ProxyServer.getInstance().getServerInfo(service.getName()))
                .ifPresentOrElse(event::setTarget, () ->
                    event.getPlayer().disconnect(new TextComponent("§cEs konnte kein passender Fallback gefunden werden."))));
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
        this.getFallback(event.getPlayer())
            .ifPresent(service -> {
                event.setCancelled(true);
                event.setCancelServer(ProxyServer.getInstance().getServerInfo(service.getName()));
            });
    }

    private Optional<IService> getFallback(final ProxiedPlayer player) {
        return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream()
            .filter(service -> service.getServiceState() == ServiceState.ONLINE)
            .filter(service -> service.getServiceVisibility() == ServiceVisibility.VISIBLE)
            .filter(service -> !service.getGroup().getGameServerVersion().isProxy())
            .filter(service -> service.getGroup().isFallbackGroup())
            .filter(service -> (player.getServer() == null || !player.getServer().getInfo().getName().equals(service.getName())))
            .min(Comparator.comparing(IService::getOnlinePlayers));
    }

}
