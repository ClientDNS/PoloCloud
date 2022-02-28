package de.polocloud.plugin.bootstrap.bungee.commands;

import de.polocloud.plugin.bootstrap.global.CloudGlobalCommand;
import de.polocloud.plugin.bootstrap.global.PlayerMessageObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BungeeCloudCommand extends Command {

    public BungeeCloudCommand() {
        super("cloud");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(commandSender instanceof ProxiedPlayer player) {
            CloudGlobalCommand.execute(player.getUniqueId(), new PlayerMessageObject() {
                @Override
                public void sendMessage(@NotNull String message) {
                    commandSender.sendMessage(TextComponent.fromLegacyText(message));
                }

                @Override
                public boolean hasPermission(@NotNull UUID uuid, @NotNull String permissions) {
                    return ProxyServer.getInstance().getPlayer(uuid).hasPermission(permissions);
                }
            }, strings);
        }
    }
}
