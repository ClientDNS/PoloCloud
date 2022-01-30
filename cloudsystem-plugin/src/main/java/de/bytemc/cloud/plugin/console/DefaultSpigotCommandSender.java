package de.bytemc.cloud.plugin.console;

import de.bytemc.cloud.plugin.bootstrap.spigot.SpigotBootstrap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DefaultSpigotCommandSender extends DefaultProxyCommandSender {

    @Override
    public void sendMessage(final @NotNull String text) {
        JavaPlugin.getPlugin(SpigotBootstrap.class).getLogger().info(text);
    }

}
