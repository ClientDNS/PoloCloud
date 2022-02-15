package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;

public final class ClearCommand extends CloudCommand {

    public ClearCommand() {
        super("clear", "Clears the console");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        cloudAPI.getLoggerProvider().clearConsole();
    }

}
