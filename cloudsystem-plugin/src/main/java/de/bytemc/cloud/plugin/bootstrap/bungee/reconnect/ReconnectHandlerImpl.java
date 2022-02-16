package de.bytemc.cloud.plugin.bootstrap.bungee.reconnect;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class ReconnectHandlerImpl implements ReconnectHandler {

    @Override
    public ServerInfo getServer(ProxiedPlayer player) {
        return ProxyServer.getInstance().getServerInfo("fallback");
    }

    @Override
    public void setServer(ProxiedPlayer player) {}

    @Override
    public void save() {}

    @Override
    public void close() {}

}
