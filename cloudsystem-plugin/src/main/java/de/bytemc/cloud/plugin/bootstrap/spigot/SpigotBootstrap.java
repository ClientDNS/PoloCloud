package de.bytemc.cloud.plugin.bootstrap.spigot;

import de.bytemc.cloud.plugin.CloudPlugin;
import de.bytemc.cloud.plugin.IPlugin;
import de.bytemc.cloud.plugin.console.DefaultSpigotCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBootstrap extends JavaPlugin implements IPlugin {

    @Override
    public void onLoad() {
        CloudPlugin.setCommandSender(new DefaultSpigotCommandSender());
        new CloudPlugin(this);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void shutdown() {
        Bukkit.getScheduler().runTask(this, () -> Bukkit.shutdown());
    }
}
