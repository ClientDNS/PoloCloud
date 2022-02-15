package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;

public final class ServiceCommand extends CloudCommand {

    public ServiceCommand() {
        super("service", "Manage services", "ser");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {
        final var log = cloudAPI.getLoggerProvider();

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (final IService service : cloudAPI.getServiceManager().getAllCachedServices()) {
                log.logMessage("Name of service '§b" + service.getName()
                    + "§7' (§7State of service '§b" + service.getServiceState().getName()
                    + "§7' | Node: '" + service.getServiceGroup().getNode() + "')");
            }
            return;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("stop")) {
            cloudAPI.getServiceManager().getService(args[1]).ifPresentOrElse(service -> {
                if (service.getServiceState() == ServiceState.PREPARED || service.getServiceState() == ServiceState.STOPPING) {
                    log.logMessage("This service ist not started or already in stopping state.", LogType.WARNING);
                    return;
                }

                service.stop();
                log.logMessage("The service '§b" + service.getName() + "§7' is now stopped.");
            }, () -> log.logMessage("This service does not exists.", LogType.WARNING));
            return;
        } else if (args.length == 4) {
            // TODO
            return;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            cloudAPI.getServiceManager().getService(args[1]).ifPresentOrElse(service -> {
                log.logMessage("Service information:");
                log.logMessage("Name: §b" + service.getName());
                log.logMessage("ID: §b" + service.getServiceID());
                log.logMessage("Group: §b" + service.getServiceGroup().getName());
                log.logMessage("Host: §b" + service.getHostName());
                log.logMessage("Port: §b" + service.getPort());
            }, () -> log.logMessage("The service does not exists.", LogType.WARNING));
            return;
        } else if (args.length > 1 && args[0].equalsIgnoreCase("command")) {
            cloudAPI.getServiceManager().getService(args[1]).ifPresentOrElse(service -> {
                final var stringBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) stringBuilder.append(args[i]).append(" ");
                final var command = stringBuilder.toString();
                service.executeCommand(command);
                log.logMessage("Executed command '" + command + "' on service " + service.getName());
            }, () -> log.logMessage("The service does not exists.", LogType.WARNING));
            return;
        }

        log.logMessage("§7Use following command: §bservice list §7- List all available services.");
        log.logMessage("§7Use following command: §bservice start (name) §7- Starting a specific service that not exists.");
        log.logMessage("§7Use following command: §bservice stop (name) §7- Stopping a specific service that exists.");
        log.logMessage("§7Use following command: §bservice info (name) §7- Prints information about the specific service.");
        log.logMessage("§7Use following command: §bservice command (name) (command) §7- Executes a command on a server.");
    }

}
