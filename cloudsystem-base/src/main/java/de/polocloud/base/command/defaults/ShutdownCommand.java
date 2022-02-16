package de.polocloud.base.command.defaults;

import de.polocloud.base.Base;
import de.polocloud.api.CloudAPI;
import de.polocloud.base.command.CloudCommand;

public final class ShutdownCommand extends CloudCommand {

    public ShutdownCommand() {
        super("stop", "Stops the cloudsystem", "exit");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        Base.getInstance().onShutdown();
    }

}
