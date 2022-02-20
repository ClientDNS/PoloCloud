package de.polocloud.base.command.defaults;

import de.polocloud.base.Base;
import de.polocloud.api.CloudAPI;
import de.polocloud.base.command.CloudCommand;
import de.polocloud.api.logger.Logger;

public final class InfoCommand extends CloudCommand {

    public InfoCommand() {
        super("info", "Prints information about the cloud");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        final Logger logger = cloudAPI.getLogger();

        logger.log("§7Version: §b" + Base.getInstance().getVersion());
        logger.log("§7Node: §b" + Base.getInstance().getNode().getName());
        logger.log("§7Threads: §b" + Thread.getAllStackTraces().keySet().size());
        logger.log("§7RAM: §b" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024)
            + "/" + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "mb");
    }

}
