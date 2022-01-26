package de.bytemc.cloud.plugin.bootstrap.proxy;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.plugin.CloudPlugin;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.stream.Collectors;

public class ProxyBootstrap extends Plugin {

    @Override
    public void onLoad() {
        new CloudPlugin();
    }

    @Override
    public void onEnable() {
        System.out.println(String.join(", ", CloudAPI.getInstance().getGroupManager().getAllCachedServiceGroups().stream().map(it -> it.getGroup()).collect(Collectors.toList())));
    }

    @Override
    public void onDisable() {

    }
}
