package de.polocloud.base.command.defaults;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.service.CloudService;
import de.polocloud.base.command.CloudCommand;
import de.polocloud.base.service.LocalService;

import java.util.Arrays;
import java.util.List;

public final class ScreenCommand extends CloudCommand {

    public ScreenCommand() {
        super("screen", "Opens a screen", "scr");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        cloudAPI.getServiceManager().getService(args[0]).ifPresentOrElse(
            cloudService -> {
                if (cloudService instanceof LocalService localService) {
                    localService.setScreen(!localService.isScreen());
                } else {
                    cloudAPI.getLogger().log("The service must be on this node!", LogType.WARNING);
                }
            },
            () -> cloudAPI.getLogger().log("This service does not exists.", LogType.WARNING));
    }

    @Override
    public List<String> tabComplete(String[] arguments) {
        if (arguments.length == 0) {
            return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream().map(CloudService::getName).toList();
        }
        return super.tabComplete(arguments);
    }

}
