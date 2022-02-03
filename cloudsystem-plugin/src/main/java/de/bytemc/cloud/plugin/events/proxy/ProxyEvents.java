package de.bytemc.cloud.plugin.events.proxy;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.services.IService;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Comparator;

public final class ProxyEvents implements Listener {

    @EventHandler
    public void handle(LoginEvent event) {

        //TODO IMPROVE WHITELIST
        final String name = event.getConnection().getName();

        if (name.equalsIgnoreCase("HttpMarco")
            || name.equalsIgnoreCase("Siggii")
            || name.equalsIgnoreCase("xImNoxh")
            || name.equalsIgnoreCase("BauHD")
            || name.equalsIgnoreCase("FallenBreak")
            || name.equalsIgnoreCase("ipommes")
            || name.equalsIgnoreCase("SilenceCode")
            || name.equalsIgnoreCase("outroddet_")
            || name.equalsIgnoreCase("Forumat")
            || name.equalsIgnoreCase("Sogares")
            || name.equalsIgnoreCase("Einfxch")
            || name.equalsIgnoreCase("NervigesLilli")) {

            CloudAPI.getInstance().getCloudPlayerManager().registerCloudPlayer(event.getConnection().getUniqueId(), name);
        } else {
            event.setCancelReason(new TextComponent("§cMomentan befindet sich das Netzwerk im Wartungsmodus."));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(ServerConnectEvent event) {
        if (event.getTarget().getName().equalsIgnoreCase("fallback")) {
            CloudAPI.getInstance().getServiceManager().getAllPossibleOnlineFallbackServices()
                .stream()
                .min(Comparator.comparing(IService::getOnlinePlayers))
                .map(it -> ProxyServer.getInstance().getServerInfo(it.getName())).ifPresentOrElse(event::setTarget, () -> {
                    event.getPlayer().disconnect(new TextComponent("§cEs konnte kein passender fallback gefunden werden."));
                });
        }
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        CloudAPI.getInstance().getCloudPlayerManager().unregisterCloudPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    @EventHandler
    public void handle(ProxyPingEvent event) {
        final ServerPing response = event.getResponse();
        final ServerPing.Players players = response.getPlayers();

        response.setPlayers(new ServerPing.Players(players.getMax(),
            CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayerOnlineAmount(), players.getSample()));

        event.setResponse(response);
    }
}
