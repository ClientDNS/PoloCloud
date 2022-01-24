package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;

public class ServiceCloudCommand extends CloudCommand {

    public ServiceCloudCommand() {
        super("service", "Manage services", ExecutorType.CONSOLE);
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

            if(service.getServiceState() == ServiceState.PREPARED || service.getServiceState() == ServiceState.STOPPING) {
                log.logMessage("This service ist not started or already in stopping state.", LogType.WARNING);
                return;
            }

            ((SimpleService) service).shutdown();
            log.logMessage("The service '§b"+ service.getName() + "§7' is now stopped.");
            //TODO CHECK QUEUE
            return;
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("start")) {
                String serviceName = args[1];
            }
            return;
        }
        log.logMessage("Usage:");
        log.logMessage("service <list> | List all available services");
        log.logMessage("service <start> (name) | Starting a specific service that exists");
        log.logMessage("service <stop> (name) | Stopping a specific service that exists");
    }
}
