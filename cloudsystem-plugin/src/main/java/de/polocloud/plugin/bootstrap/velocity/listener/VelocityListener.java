package de.polocloud.plugin.bootstrap.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.player.impl.SimpleCloudPlayer;
import de.polocloud.plugin.bootstrap.velocity.VelocityBootstrap;
import de.polocloud.wrapper.Wrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record VelocityListener(VelocityBootstrap bootstrap, ProxyServer proxyServer) {

    @Subscribe
    public void handle(final @NotNull LoginEvent event) {
        final var player = event.getPlayer();

        if (CloudAPI.getInstance().getPlayerManager().getOnlineCount() >= Wrapper.getInstance().thisService().getMaxPlayers()) {
            if (!player.hasPermission("cloud.network.full.join")) {
                player.disconnect(Component
                    .text("This network has reached the maximum number of players.")
                    .color(NamedTextColor.RED));
                return;
            }
        }

        Wrapper.getInstance().getPlayerManager().registerCloudPlayer(
            new SimpleCloudPlayer(player.getUniqueId(), player.getUsername(), Wrapper.getInstance().thisService()));
    }

    @Subscribe
    public void handle(final @NotNull PlayerChooseInitialServerEvent event) {
        event.setInitialServer(this.bootstrap.getFallback(event.getPlayer())
            .flatMap(service -> this.proxyServer.getServer(service.getName()))
            .orElse(null));
    }

    @Subscribe
    public void handle(final @NotNull ServerConnectedEvent event) {
        final var player = event.getPlayer();

        CloudAPI.getInstance().getPlayerManager().getCloudPlayer(player.getUniqueId())
            .ifPresent(cloudPlayer -> {
                cloudPlayer.setServer(Objects.requireNonNull(CloudAPI.getInstance().getServiceManager()
                    .getServiceByNameOrNull(event.getServer().getServerInfo().getName())));
                cloudPlayer.update(CloudPlayerUpdateEvent.UpdateReason.SERVER_SWITCH);
            });
    }

    @Subscribe
    public void handle(final @NotNull DisconnectEvent event) {
        if (event.getLoginStatus() == DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN
            || event.getLoginStatus() == DisconnectEvent.LoginStatus.PRE_SERVER_JOIN) {
            Wrapper.getInstance().getPlayerManager().unregisterCloudPlayer(event.getPlayer().getUniqueId());
        }
    }

    @Subscribe
    public void handle(final @NotNull ProxyPingEvent event) {
        event.setPing(event.getPing().asBuilder()
            .onlinePlayers(Wrapper.getInstance().getPlayerManager().getOnlineCount())
            .maximumPlayers(Wrapper.getInstance().thisService().getMaxPlayers())
            .build());
    }

    @Subscribe
    public void handle(final @NotNull KickedFromServerEvent event) {
        if (event.getPlayer().isActive()) {
            this.bootstrap.getFallback(event.getPlayer()).flatMap(service -> this.proxyServer.getServer(service.getName()))
                .ifPresent(registeredServer -> {
                    if (event.getServer() != null && event.getServer().getServerInfo().getName().equals(registeredServer.getServerInfo().getName())) {
                        event.setResult(KickedFromServerEvent.Notify.create(event.getServerKickReason().orElse(Component.empty())));
                    } else {
                        event.setResult(KickedFromServerEvent.RedirectPlayer.create(registeredServer));
                    }
                });
        }
    }

}
