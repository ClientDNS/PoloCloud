package de.polocloud.base.command.defaults;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.service.ServiceState;
import de.polocloud.base.Base;
import de.polocloud.base.command.CloudCommand;
import de.polocloud.base.service.LocalService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@CloudCommand.Command(name = "service", description = "Manage services", aliases = "ser")
public final class ServiceCommand extends CloudCommand {

    @Override
    public void execute(Base base, String[] args) {
        final var logger = base.getLogger();

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            base.getServiceManager().getAllCachedServices().forEach(it -> logger
                .log("§7Name of service '§b" + it.getName() + "§7' (§7State of service '§b" + it.getState() + "§7' | Node: '" + it.getGroup().getNode() + "')"));
            return;
        } else if (args.length >= 1) {
            base.getServiceManager().getService(args[0]).ifPresentOrElse(service -> {
                if (args.length == 4 && args[1].equalsIgnoreCase("edit")) {
                    final var key = args[2].toLowerCase();

                    if (key.equals("maxplayers")) {
                        this.getAndSetInt(key, args[3], service, service::setMaxPlayers);
                        logger.log("§7Successfully set max players count to " + args[3]);
                        return;
                    }
                    return;
                }

                if (args.length == 2 && args[1].equalsIgnoreCase("stop")) {
                    if (service.getState().equals(ServiceState.PREPARED) || service.getState().equals(ServiceState.STOPPED)) {
                        logger.log("§7This service ist §cnot §7started or already in §cstopping §7state.", LogType.WARNING);
                        return;
                    }
                    service.stop();
                    logger.log("§7The service '§b" + service.getName() + "§7' is now §cstopped§7.");
                } else if (args.length == 2 && args[1].equalsIgnoreCase("copy")) {
                    if (service instanceof LocalService localService) {
                        final var template = new File("templates/" + localService.getGroup().getTemplate());
                        try {
                            FileUtils.copyDirectory(localService.getWorkingDirectory(), template);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        logger.log("§aSuccessfully §7moved all files into the template.");
                    } else {
                        logger.log("§7The service §cmust §7be a service on §ethis §7node.", LogType.WARNING);
                    }
                } else if (args.length > 1 && args[1].equalsIgnoreCase("command")) {
                    final var stringBuilder = new StringBuilder();
                    for (var i = 2; i < args.length; i++) stringBuilder.append(args[i]).append(" ");
                    final var command = stringBuilder.toString();
                    service.executeCommand(command);
                    logger.log("§7Executed command '§b" + command + "§7' on service §b" + service.getName());
                } else {
                    logger.log("Service information:",
                        "Name: §b" + service.getName(),
                        "State: §b" + service.getState(),
                        "Players: §b" + service.getOnlineCount() + " &7/ Max: §b" + service.getMaxPlayers(),
                        "Host: §b" + service.getHostName() + " &7/ Port: §b" + service.getPort(),
                        "Motd: §b" + service.getMotd());

                    //get current service memory
                    ((LocalService)service).getMemory().thenAccept(integer -> logger.log("Current memory: §b" + integer + "mb"));
                }

            }, () -> logger.log("This service does not exists.", LogType.WARNING));
            return;
        }

        final var help = "§7Use following command: §b";
        logger.log(
            help + "service list §7- List all available services.",
            help + "service (name) start §7- Starting a specific service that not exists.",
            help + "service (name) stop §7- Stopping a specific service that exists.",
            help + "service (name) §7- Prints information about the specific service.",
            help + "service (name) command (command) §7- Executes a command on a server.",
            help + "service (name) edit (key) (value) §7- Change properties of online service.",
            help + "service (name) copy §7- Copies all files of the service to the template.");
    }

    @Override
    public List<String> tabComplete(String[] arguments) {
        if (arguments.length == 1) {
            return Base.getInstance().getServiceManager().getAllCachedServices().stream().map(CloudService::getName).toList();
        } else if (arguments.length == 2) {
            return Arrays.asList("list", "start", "stop", "command");
        }
        return super.tabComplete(arguments);
    }

    private void getAndSetInt(final String key, final String value, final CloudService service, final Consumer<Integer> consumer) {
        try {
            consumer.accept(Integer.parseInt(value));
            service.update();
        } catch (NumberFormatException e) {
            CloudAPI.getInstance().getLogger()
                .log("§7Use following command: §bservice " + service.getName() + " edit " + key + " §7(§bint§7)");
        }
    }

}
