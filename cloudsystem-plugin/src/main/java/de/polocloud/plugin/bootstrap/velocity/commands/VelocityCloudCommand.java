package de.polocloud.plugin.bootstrap.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.polocloud.plugin.bootstrap.global.CloudGlobalCommand;
import de.polocloud.plugin.bootstrap.global.PlayerMessageObject;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class VelocityCloudCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        if(invocation.source() instanceof Player player) {
            CloudGlobalCommand.execute(player.getUniqueId(), new PlayerMessageObject() {
                @Override
                public void sendMessage(@NotNull String message) {
                    invocation.source().sendMessage(Component.text(message));
                }

                @Override
                public boolean hasPermission(@NotNull UUID uuid, @NotNull String permission) {
                    return invocation.source().hasPermission(permission);
                }
            }, invocation.arguments());
        }
    }
}
