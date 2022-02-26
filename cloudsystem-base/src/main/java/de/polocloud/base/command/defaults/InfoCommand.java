package de.polocloud.base.command.defaults;

import de.polocloud.base.Base;
import de.polocloud.base.command.CloudCommand;

public final class InfoCommand extends CloudCommand {

    public InfoCommand() {
        super("info", "Prints information about the cloud");
    }

    @Override
    public void execute(Base base, String[] args) {
        final var logger = base.getLogger();

        logger.log("§7Version: §b" + Base.getInstance().getVersion());
        logger.log("§7Node: §b" + Base.getInstance().getNode().getName());
        logger.log("§7Threads: §b" + Thread.getAllStackTraces().keySet().size());
        logger.log("§7RAM: §b" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024)
            + "/" + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "mb");
    }

}
