package de.bytemc.cloud.plugin.events.proxy;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.fallback.FallbackHandler;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.wrapper.service.ServiceManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public final class ProxyEvents implements Listener {

    private final ICloudPlayerManager playerManager;

    public ProxyEvents() {
        this.playerManager = CloudAPI.getInstance().getCloudPlayerManager();
    }

    //TODO WHITELIST
    private static final List<String> whitelistedPlayers = Lists.newArrayList(
        "HttpMarco", "Siggii", "xImNoxh", "BauHD", "FallenBreak", "ipommes", "SilenceCode", "outroddet_", "Forumat", "Einfxch", "NervigesLilli");

    @EventHandler
    public void handle(PreLoginEvent event) {

        final String name = event.getConnection().getName();

        if(!whitelistedPlayers.contains(event.getConnection().getName())) {
            event.setCancelReason(new TextComponent("§cDu besitzt momentan keinen Zuganng, um das §nNetzwerk §czu betreten."));
            event.setCancelled(true);
            return;
        }

        if (!FallbackHandler.isFallbackAvailable()) {
            event.setCancelReason(new TextComponent("§cEs konnte kein passender Fallback gefunden werden."));
            event.setCancelled(true);
            return;
        }
        playerManager.registerCloudPlayer(event.getConnection().getUniqueId(), name);
    }

    @EventHandler
    public void handle(ServerConnectEvent event) {
        ICloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayerByNameOrNull(event.getPlayer().getName());
        if (event.getTarget().getName().equalsIgnoreCase("fallback")) {
            FallbackHandler.getLobbyFallbackOrNull().ifPresentOrElse(it -> {
                event.setTarget(ProxyServer.getInstance().getServerInfo(it.getName()));
                cloudPlayer.setServer(it);
                cloudPlayer.setProxyServer(((ServiceManager) CloudAPI.getInstance().getServiceManager()).thisService());
                cloudPlayer.update();
            }, () -> {
                event.getPlayer().disconnect(new TextComponent("§cEs konnte kein passender Fallback gefunden werden."));
            });
        } else {
            cloudPlayer.setServer(CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(event.getTarget().getName()));
            cloudPlayer.setProxyServer(((ServiceManager) CloudAPI.getInstance().getServiceManager()).thisService());
            cloudPlayer.update();
        }
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        playerManager.unregisterCloudPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    @EventHandler
    public void handle(ProxyPingEvent event) {
        final ServerPing response = event.getResponse();
        final ServerPing.Players players = response.getPlayers();

        response.setPlayers(new ServerPing.Players(((ServiceManager) CloudAPI.getInstance().getServiceManager()).thisService().getMaxPlayers(), playerManager.getCloudPlayerOnlineAmount(), players.getSample()));
        event.setResponse(response);
    }
}
