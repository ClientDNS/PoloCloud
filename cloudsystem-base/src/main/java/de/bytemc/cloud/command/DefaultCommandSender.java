package de.bytemc.cloud.command;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;

public class DefaultCommandSender implements ICommandSender {

    @Override
    public void sendMessage(String text) {
        CloudAPI.getInstance().getLoggerProvider().logMessage(text);
    }

    @Override
    public ExecutorType getCommandType() {
        return ExecutorType.CONSOLE;
    }

}
