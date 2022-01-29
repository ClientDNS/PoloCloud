package de.bytemc.cloud.plugin.console;

import de.bytemc.cloud.plugin.bootstrap.spigot.SpigotBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultSpigotCommandSender extends DefaultProxyCommandSender {

    @Override
    public void sendMessage(final String text) {
        JavaPlugin.getPlugin(SpigotBootstrap.class).getLogger().info(text);
    }

}
