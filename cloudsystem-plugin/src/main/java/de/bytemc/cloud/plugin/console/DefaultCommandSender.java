package de.bytemc.cloud.plugin.console;

import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;

public class DefaultCommandSender implements ICommandSender {

    @Override
    public void sendMessage(String text) {
        System.out.println(text);
    }

    @Override
    public ExecutorType getCommandType() {
        return ExecutorType.CONSOLE;
    }
}
