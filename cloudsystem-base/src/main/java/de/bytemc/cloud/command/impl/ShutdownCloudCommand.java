package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;

public class ShutdownCloudCommand extends CloudCommand {

    public ShutdownCloudCommand() {
        super("stop","Stop the cloudsystem", ExecutorType.CONSOLE);
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        Base.getInstance().onShutdown();
    }


}
