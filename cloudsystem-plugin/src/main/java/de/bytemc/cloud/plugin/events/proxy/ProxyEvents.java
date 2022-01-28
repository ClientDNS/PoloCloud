package de.bytemc.cloud.plugin.events.proxy;

import de.bytemc.cloud.api.CloudAPI;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyEvents implements Listener {

    @EventHandler
    public void handle(LoginEvent event){
        CloudAPI.getInstance().getCloudPlayerManager().registerCloudPlayer(event.getConnection().getUniqueId(), event.getConnection().getName());
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event){
        CloudAPI.getInstance().getCloudPlayerManager().unregisterCloudPlayer(event.getPlayer().getUniqueId());
    }

}
