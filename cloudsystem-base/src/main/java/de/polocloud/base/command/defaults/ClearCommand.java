package de.polocloud.base.command.defaults;

import de.polocloud.api.CloudAPI;
import de.polocloud.base.command.CloudCommand;

public final class ClearCommand extends CloudCommand {

    public ClearCommand() {
        super("clear", "Clears the console");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        cloudAPI.getLogger().clearConsole();
    }

}
