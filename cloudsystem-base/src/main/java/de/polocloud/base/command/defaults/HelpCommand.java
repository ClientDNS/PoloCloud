package de.polocloud.base.command.defaults;

import de.polocloud.base.Base;
import de.polocloud.api.CloudAPI;
import de.polocloud.base.command.CloudCommand;

public final class HelpCommand extends CloudCommand {

    public HelpCommand() {
        super("help", "All commands and help descriptions");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        final var manager = Base.getInstance().getCommandManager();

        cloudAPI.getLogger().log("All possible commands(§b" + manager.getCachedCloudCommands().size() + "§7):");
        manager.getCachedCloudCommands().values().forEach(it -> cloudAPI.getLogger()
            .log("§b" + it.getName() + getAliases(it) + " - " + it.getDescription()));
    }

    private String getAliases(CloudCommand command) {
        return command.getAliases().length == 0 ? "" : "§7(§b" + String.join(", ", command.getAliases()) + "§7)";
    }

}
