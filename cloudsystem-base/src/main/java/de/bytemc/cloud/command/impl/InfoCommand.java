package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.logger.Logger;

public final class InfoCommand extends CloudCommand {

    public InfoCommand() {
        super("info", "Prints information about the cloud");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        final Logger logger = cloudAPI.getLogger();

        logger.log("§7Version: §b" + Base.getInstance().getVersion());
        logger.log("§7Node: §b" + Base.getInstance().getNode().getNodeName());
        logger.log("§7Threads: §b" + Thread.getAllStackTraces().keySet().size());
        logger.log("§7RAM: §b" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000) + "mb");
    }

}
