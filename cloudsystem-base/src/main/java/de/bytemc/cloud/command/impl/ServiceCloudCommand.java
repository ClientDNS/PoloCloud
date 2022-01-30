package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.services.ServiceManager;

public class ServiceCloudCommand extends CloudCommand {

    public ServiceCloudCommand() {
        super("service", "Manage services", ExecutorType.CONSOLE, "ser");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {

        var log = CloudAPI.getInstance().getLoggerProvider();

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (IService service : CloudAPI.getInstance().getServiceManager().getAllCachedServices()) {
                log.logMessage("Name of service '§b" + service.getName() + "§7' (§7State of service '§b" + service.getServiceState().getName() + "§7' | Node: '" + service.getServiceGroup().getNode() + "')");
            }
            return;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("stop")) {

            IService service = CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(args[1]);

            if (service == null) {
                log.logMessage("This service does not exists.", LogType.WARNING);
                return;
            }

            if (service.getServiceState() == ServiceState.PREPARED || service.getServiceState() == ServiceState.STOPPING) {
                log.logMessage("This service ist not started or already in stopping state.", LogType.WARNING);
                return;
            }

            ((ServiceManager) CloudAPI.getInstance().getServiceManager()).shutdownService(service);
            log.logMessage("The service '§b" + service.getName() + "§7' is now stopped.");
            return;
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("start")) {
                String serviceName = args[1];
            }
            return;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {

            IService service = CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(args[1]);

            if (service == null) {
                log.logMessage("This service does not exists.", LogType.WARNING);
                return;
            }

            log.logMessage("Service information:");
            log.logMessage("Name: §b" + service.getName());
            log.logMessage("ID: §b" + service.getServiceID());
            log.logMessage("Group: §b" + service.getServiceGroup().getName());
            log.logMessage("Host: §b" + service.getHostName());
            log.logMessage("Port: §b" + service.getPort());
            return;
        }
        log.logMessage("§7Use following command: §bservice list §7- List all available services.");
        log.logMessage("§7Use following command: §bservice start (name) §7- Starting a specific service that exists.");
        log.logMessage("§7Use following command: §bservice stop (name) §7- Stopping a specific service that exists.");
        log.logMessage("§7Use following command: §bservice info (name) §7- Prints information about the specific service.");
    }

}
