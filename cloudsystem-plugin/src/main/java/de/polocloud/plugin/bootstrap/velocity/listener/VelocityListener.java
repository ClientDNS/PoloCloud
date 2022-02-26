package de.polocloud.plugin.bootstrap.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.player.impl.SimpleCloudPlayer;
import de.polocloud.plugin.bootstrap.velocity.VelocityBootstrap;
import de.polocloud.wrapper.Wrapper;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Objects;

public record VelocityListener(VelocityBootstrap bootstrap, ProxyServer proxyServer) {

    @Subscribe
    public void handle(final LoginEvent event) {
        final var player = event.getPlayer();

        Wrapper.getInstance().getPlayerManager().registerCloudPlayer(
            new SimpleCloudPlayer(player.getUniqueId(), player.getUsername(), Wrapper.getInstance().thisService()));
    }

    @Subscribe
    public void handle(final ServerPreConnectEvent event) {
        final var player = event.getPlayer();

        if (player.getCurrentServer().isEmpty()) {

            if(CloudAPI.getInstance().getPlayerManager().getOnlineCount() >= Wrapper.getInstance().thisService().getMaxPlayers()){
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                player.disconnect(Component.text("§cThis network has reached the maximum number of players."));
                return;
            }


            this.bootstrap.getFallback(player).flatMap(service -> this.proxyServer.getServer(service.getName()))
                .ifPresentOrElse(
                    registeredServer -> event.setResult(ServerPreConnectEvent.ServerResult.allowed(registeredServer)),
                    () -> {
                        event.setResult(ServerPreConnectEvent.ServerResult.denied());
                        player.disconnect(Component.text("§cNo fallback could be found."));
                    });
        }
    }

    @Subscribe
    public void handle(final ServerConnectedEvent event) {
        final var player = event.getPlayer();

        CloudAPI.getInstance().getPlayerManager().getCloudPlayer(player.getUniqueId())
            .ifPresent(cloudPlayer -> {
                cloudPlayer.setServer(Objects.requireNonNull(CloudAPI.getInstance().getServiceManager()
                    .getServiceByNameOrNull(event.getServer().getServerInfo().getName())));
                cloudPlayer.update(CloudPlayerUpdateEvent.UpdateReason.SERVER_SWITCH);
            });
    }

    @Subscribe
    public void handle(final DisconnectEvent event) {
        Wrapper.getInstance().getPlayerManager().unregisterCloudPlayer(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void handle(final ProxyPingEvent event) {
        event.setPing(event.getPing().asBuilder()
            .onlinePlayers(Wrapper.getInstance().getPlayerManager().getOnlineCount())
            .maximumPlayers(Wrapper.getInstance().thisService().getMaxPlayers())
            .build());
    }

}
