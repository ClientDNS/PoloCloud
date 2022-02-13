package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import de.bytemc.cloud.api.logger.LoggerProvider;

public final class InfoCloudCommand extends CloudCommand {

    public InfoCloudCommand() {
        super("info", "Prints information about the cloud");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        final LoggerProvider loggerProvider = Base.getInstance().getLoggerProvider();

        loggerProvider.logMessage("§7Version: §b" + Base.getInstance().getVersion());
        loggerProvider.logMessage("§7Node: §b" + Base.getInstance().getNode().getNodeName());
        loggerProvider.logMessage("§7Threads: §b" + Thread.getAllStackTraces().keySet().size());
        loggerProvider.logMessage("§7RAM: §b" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000) + "mb");
    }

}
