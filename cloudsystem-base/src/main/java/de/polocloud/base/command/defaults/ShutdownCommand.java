package de.polocloud.base.command.defaults;

import de.polocloud.base.Base;
import de.polocloud.base.command.CloudCommand;

@CloudCommand.Command(name = "stop", description = "Stops the cloudsystem", aliases = "exit")
public final class ShutdownCommand extends CloudCommand {

    @Override
    public void execute(Base base, String[] args) {
        base.onShutdown();
    }

}
