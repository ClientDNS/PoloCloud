package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;

public final class ClearCloudCommand extends CloudCommand {

    public ClearCloudCommand() {
        super("clear", "Clears the console", ExecutorType.CONSOLE);
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        CloudAPI.getInstance().getLoggerProvider().clearConsole();
    }

}
