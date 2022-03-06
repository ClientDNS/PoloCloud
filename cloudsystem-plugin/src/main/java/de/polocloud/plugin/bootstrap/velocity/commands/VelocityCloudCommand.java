package de.polocloud.plugin.bootstrap.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import de.polocloud.plugin.bootstrap.global.CloudGlobalCommand;
import de.polocloud.plugin.bootstrap.global.PlayerMessageObject;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class VelocityCloudCommand implements SimpleCommand {

    @Override
    public void execute(final @NotNull Invocation invocation) {
        CloudGlobalCommand.execute(new PlayerMessageObject() {
            @Override
            public void sendMessage(@NotNull String message) {
                invocation.source().sendMessage(Component.text(message));
            }

            @Override
            public boolean hasPermission(@NotNull String permission) {
                return invocation.source().hasPermission(permission);
            }
        }, invocation.arguments());
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return CloudGlobalCommand.tabComplete(invocation.arguments(), s -> invocation.source().hasPermission(s));
    }
}
