package de.polocloud.plugin.bootstrap.bungee.commands;

import de.polocloud.plugin.bootstrap.global.CloudGlobalCommand;
import de.polocloud.plugin.bootstrap.global.PlayerMessageObject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

public class BungeeCloudCommand extends Command implements TabExecutor {

    public BungeeCloudCommand() {
        super("cloud");
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] arguments) {
        CloudGlobalCommand.execute(new PlayerMessageObject() {
            @Override
            public void sendMessage(@NotNull String message) {
                commandSender.sendMessage(TextComponent.fromLegacyText(message));
            }

            @Override
            public boolean hasPermission(@NotNull String permissions) {
                return commandSender.hasPermission(permissions);
            }
        }, arguments);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        return CloudGlobalCommand.tabComplete(args, s -> commandSender.hasPermission(s));
    }
}
