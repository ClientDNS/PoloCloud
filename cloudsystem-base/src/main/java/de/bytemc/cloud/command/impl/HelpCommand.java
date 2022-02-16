package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;

public final class HelpCommand extends CloudCommand {

    public HelpCommand() {
        super("help", "All commands and help descriptions");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        final var manager = Base.getInstance().getCommandManager();

        cloudAPI.getLogger().logMessage("All possible commands(§b" + manager.getCachedCloudCommands().size() + "§7):");
        manager.getCachedCloudCommands().values().forEach(it -> cloudAPI.getLogger()
            .logMessage("§b" + it.getName() + getAliases(it) + " - " + it.getDescription()));
    }

    private String getAliases(CloudCommand command) {
        return command.getAliases().length == 0 ? "" : "§7(§b" + String.join(", ", command.getAliases()) + "§7)";
    }

}
