package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;

public final class ShutdownCommand extends CloudCommand {

    public ShutdownCommand() {
        super("stop", "Stops the cloudsystem", "exit");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        Base.getInstance().onShutdown();
    }

}
