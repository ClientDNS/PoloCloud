package de.bytemc.cloud.plugin.bootstrap;

import de.bytemc.cloud.plugin.CloudPlugin;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyBootstrap extends Plugin {

    @Override
    public void onLoad() {
        new CloudPlugin();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {

    }
}
