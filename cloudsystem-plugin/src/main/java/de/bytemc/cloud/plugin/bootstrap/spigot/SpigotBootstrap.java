package de.bytemc.cloud.plugin.bootstrap.spigot;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.plugin.IPlugin;
import de.bytemc.cloud.wrapper.service.ServiceManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBootstrap extends JavaPlugin implements IPlugin {

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        System.out.println("default: " + CloudAPI.getInstance().getGroupManager().getServiceGroupByNameOrNull("pxt").getDefaultMaxPlayers());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void shutdown() {
        Bukkit.getScheduler().runTask(this, Bukkit::shutdown);
    }

}
