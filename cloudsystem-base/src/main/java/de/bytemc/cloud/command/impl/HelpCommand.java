package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.command.CommandManager;

public final class HelpCommand extends CloudCommand {

    public HelpCommand() {
        super("help", "All commands and help descriptions");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        CommandManager manager = cloudAPI.getCommandManager();

        cloudAPI.getLoggerProvider().logMessage("All possible commands(§b" + manager.getCachedCloudCommands().size() + "§7):");
        manager.getCachedCloudCommands().forEach(it -> cloudAPI.getLoggerProvider().logMessage("§b" + it.getCommandName() + getAlias(it) + " - " + it.getDescription()));
    }

    private String getAlias(CloudCommand command) {
        return command.getAlias().length == 0 ? "" : "§7(§b" + String.join(", ", command.getAlias()) + "§7)";
    }

}
