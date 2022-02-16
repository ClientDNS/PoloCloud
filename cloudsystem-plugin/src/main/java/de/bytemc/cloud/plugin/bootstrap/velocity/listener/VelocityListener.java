package de.bytemc.cloud.plugin.bootstrap.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudPlayerUpdateEvent;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;
import de.bytemc.cloud.plugin.bootstrap.velocity.VelocityBootstrap;
import de.bytemc.cloud.wrapper.Wrapper;

import java.util.Objects;

public record VelocityListener(VelocityBootstrap bootstrap, ProxyServer proxyServer) {

    @Subscribe
    public void handle(final LoginEvent event) {
        final var player = event.getPlayer();

        Wrapper.getInstance().getCloudPlayerManager().registerCloudPlayer(
            new SimpleCloudPlayer(player.getUniqueId(), player.getUsername(), Wrapper.getInstance().thisService()));
    }

    @Subscribe
    public void handle(final ServerPreConnectEvent event) {
        if (event.getPlayer().getCurrentServer().isEmpty()) {
            this.bootstrap.getFallback(event.getPlayer()).flatMap(service -> this.proxyServer.getServer(service.getName()))
                .ifPresentOrElse(
                    registeredServer -> event.setResult(ServerPreConnectEvent.ServerResult.allowed(registeredServer)),
                    () -> event.setResult(ServerPreConnectEvent.ServerResult.denied()));
        }
    }

    @Subscribe
    public void handle(final ServerConnectedEvent event) {
        final var player = event.getPlayer();

        player.getCurrentServer().ifPresent(serverConnection ->
            CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(player.getUniqueId())
                .ifPresent(cloudPlayer -> {
                    cloudPlayer.setServer(Objects.requireNonNull(CloudAPI.getInstance().getServiceManager()
                        .getServiceByNameOrNull(serverConnection.getServerInfo().getName())));
                    cloudPlayer.update(CloudPlayerUpdateEvent.UpdateReason.SERVER_SWITCH);
                }));
    }

    @Subscribe
    public void handle(final DisconnectEvent event) {
        Wrapper.getInstance().getCloudPlayerManager().unregisterCloudPlayer(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void handle(final ProxyPingEvent event) {
        event.setPing(event.getPing().asBuilder()
            .onlinePlayers(Wrapper.getInstance().getCloudPlayerManager().getCloudPlayerOnlineAmount())
            .maximumPlayers(Wrapper.getInstance().thisService().getMaxPlayers())
            .build());
    }

}