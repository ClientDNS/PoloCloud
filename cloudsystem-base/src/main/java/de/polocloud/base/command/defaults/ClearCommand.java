package de.polocloud.base.command.defaults;

import de.polocloud.base.Base;
import de.polocloud.base.command.CloudCommand;

public final class ClearCommand extends CloudCommand {

    public ClearCommand() {
        super("clear", "Clears the console");
    }

    @Override
    public void execute(Base base, String[] args) {
        base.getLogger().clearConsole();
    }

}
