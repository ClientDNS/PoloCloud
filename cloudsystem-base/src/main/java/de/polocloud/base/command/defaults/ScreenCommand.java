package de.polocloud.base.command.defaults;

import de.polocloud.api.logger.LogType;
import de.polocloud.api.service.CloudService;
import de.polocloud.base.Base;
import de.polocloud.base.command.CloudCommand;
import de.polocloud.base.service.LocalService;

import java.util.List;

@CloudCommand.Command(name = "screen", description = "Opens a screen", aliases = "scr")
public final class ScreenCommand extends CloudCommand {

    @Override
    public void execute(Base base, String[] arguments) {
        if (arguments.length == 1) {
            base.getServiceManager().getService(arguments[0]).ifPresentOrElse(
                cloudService -> {
                    if (cloudService instanceof LocalService localService) {
                        localService.setScreen(!localService.isScreen());
                        if (localService.isScreen()) {
                            base.getLogger().log("The screen of service " + cloudService.getName() + " has been activated.");
                        } else {
                            base.getLogger().log("The screen of service " + cloudService.getName() + " has been deactivated.");
                        }
                        return;
                    }
                    base.getLogger().log("The service must be on this node!", LogType.WARNING);
                },
                () -> base.getLogger().log("This service does not exists.", LogType.WARNING));
        } else {
            base.getLogger().log("§7Use following command: §bscreen <Service>> - Activates/deactivates the screen of a service");
        }
    }

    @Override
    public List<String> tabComplete(String[] arguments) {
        if (arguments.length == 1) {
            return Base.getInstance().getServiceManager().getAllCachedServices().stream().map(CloudService::getName).toList();
        }
        return super.tabComplete(arguments);
    }

}
