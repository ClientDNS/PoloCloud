package de.bytemc.cloud.plugin.bootstrap.bungee.reconnect;

import de.bytemc.cloud.plugin.bootstrap.bungee.BungeeBootstrap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public record ReconnectHandlerImpl(BungeeBootstrap bootstrap) implements ReconnectHandler {

    @Override
    public ServerInfo getServer(ProxiedPlayer player) {
        return this.bootstrap.getFallback(player).map(service -> ProxyServer.getInstance().getServerInfo(service.getName()))
            .orElse(null);
    }

    @Override
    public void setServer(ProxiedPlayer player) {}

    @Override
    public void save() {}

    @Override
    public void close() {}

}
