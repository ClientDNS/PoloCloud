package de.polocloud.base.command.defaults;

import de.polocloud.base.Base;
import de.polocloud.base.command.CloudCommand;

@CloudCommand.Command(name = "clear", description = "Clears the console")
public final class ClearCommand extends CloudCommand {

    @Override
    public void execute(Base base, String[] args) {
        base.getLogger().clearConsole();
    }

}
