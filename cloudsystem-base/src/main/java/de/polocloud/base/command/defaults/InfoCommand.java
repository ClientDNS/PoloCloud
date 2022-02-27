package de.polocloud.base.command.defaults;

import de.polocloud.base.Base;
import de.polocloud.base.command.CloudCommand;

@CloudCommand.Command(name = "info", description = "Prints information about the cloud")
public final class InfoCommand extends CloudCommand {

    @Override
    public void execute(Base base, String[] args) {
        var runtime = Runtime.getRuntime();

        base.getLogger().log("§7Version: §b" + Base.getInstance().getVersion(),
            "§7Node: §b" + Base.getInstance().getNode().getName(),
            "§7Threads: §b" + Thread.getAllStackTraces().keySet().size(),
            "§7RAM: §b" + calcMemory(runtime.totalMemory() - runtime.freeMemory()) + "/" + calcMemory(runtime.maxMemory()) + "mb");
    }

    private long calcMemory(final long memory) {
        return memory / 1024 / 1024;
    }

}
