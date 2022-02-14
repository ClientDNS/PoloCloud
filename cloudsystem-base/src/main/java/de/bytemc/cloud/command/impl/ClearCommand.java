package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.command.executor.ICommandSender;

public final class ClearCommand extends CloudCommand {

    public ClearCommand() {
        super("clear", "Clears the console");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        CloudAPI.getInstance().getLoggerProvider().clearConsole();
    }

}
