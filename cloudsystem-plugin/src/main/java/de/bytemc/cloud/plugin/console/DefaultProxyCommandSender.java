package de.bytemc.cloud.plugin.console;

import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import net.md_5.bungee.api.ProxyServer;

public class DefaultProxyCommandSender implements ICommandSender {

    @Override
    public void sendMessage(final String text) {
        ProxyServer.getInstance().getLogger().info(text);
    }

    @Override
    public ExecutorType getCommandType() {
        return ExecutorType.CONSOLE;
    }

}
