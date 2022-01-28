package de.bytemc.cloud.plugin.bootstrap.proxy;

import de.bytemc.cloud.plugin.CloudPlugin;
import de.bytemc.cloud.plugin.IPlugin;
import de.bytemc.cloud.plugin.console.DefaultProxyCommandSender;
import de.bytemc.cloud.plugin.events.proxy.ProxyEvents;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class ProxyBootstrap extends Plugin implements IPlugin {

    @Override
    public void onLoad() {
        CloudPlugin.setCommandSender(new DefaultProxyCommandSender());
        new CloudPlugin(this);
    }

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new ProxyEvents());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void shutdown() {
        getProxy().getScheduler().schedule(this, () -> getProxy().stop(), 0, TimeUnit.MILLISECONDS);
    }
}
