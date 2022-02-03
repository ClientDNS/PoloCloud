package de.bytemc.cloud.plugin.console;

import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;

public class DefaultProxyCommandSender implements ICommandSender {

    @Override
    public void sendMessage(final @NotNull String text) {
        ProxyServer.getInstance().getLogger().info(text);
    }

    @Override
    public @NotNull ExecutorType getCommandType() {
        return ExecutorType.CONSOLE;
    }

}
