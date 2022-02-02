package de.bytemc.cloud.plugin.bootstrap.proxy.reconnect;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReconnectHandlerImpl implements net.md_5.bungee.api.ReconnectHandler {

    @Override
    public ServerInfo getServer(ProxiedPlayer player) {
        return ProxyServer.getInstance().getServerInfo("fallback");
    }

    @Override
    public void setServer(ProxiedPlayer player) {

    }

    @Override
    public void save() {

    }

    @Override
    public void close() {

    }
}
