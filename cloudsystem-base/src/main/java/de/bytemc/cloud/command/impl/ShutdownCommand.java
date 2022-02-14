package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.command.executor.ICommandSender;

public final class ShutdownCommand extends CloudCommand {

    public ShutdownCommand() {
        super("stop", "Stops the cloudsystem", "exit");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        Base.getInstance().onShutdown();
    }

}
