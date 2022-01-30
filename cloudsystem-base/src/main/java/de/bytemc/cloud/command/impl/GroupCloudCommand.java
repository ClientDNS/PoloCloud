package de.bytemc.cloud.command.impl;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import de.bytemc.cloud.api.groups.DefaultGroup;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.cloud.services.ServiceManager;

import java.util.function.Consumer;

public class GroupCloudCommand extends CloudCommand {

    public GroupCloudCommand() {
        super("group", "Manage the cloud groups", ExecutorType.CONSOLE);
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {

        var groupManager = CloudAPI.getInstance().getGroupManager();
        var log = CloudAPI.getInstance().getLoggerProvider();

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (final IServiceGroup serviceGroup : groupManager.getAllCachedServiceGroups()) {
                log.logMessage("Name of group '§b" + serviceGroup.getGroup() + "§7' (§7Version '§b"
                    + serviceGroup.getGameServerVersion() + "§7' | Node: '" + serviceGroup.getNode() + "')");
            }
            return;
        }
        if (args.length == 5 && args[0].equalsIgnoreCase("create")) {

            var name = args[1];

            if (groupManager.isServiceGroupExists(name)) {
                log.logMessage("This group is already exists", LogType.WARNING);
                return;
            }

            //create name memory
            try {
                var memory = Integer.parseInt(args[2]);
                var staticService = Boolean.parseBoolean(args[3]);

                var gameServerVersion = GameServerVersion.getVersionByTitle(args[4]);

                var serviceGroup = new DefaultGroup(name, memory, staticService, gameServerVersion);
                groupManager.addServiceGroup(serviceGroup);
                serviceGroup.getGameServerVersion().download();

                Base.getInstance().getGroupTemplateService().createTemplateFolder(serviceGroup);
                log.logMessage("The group '§b" + name + "§7' is now registered and online.");
                Base.getInstance().getQueueService().checkForQueue();
                return;
            } catch (NumberFormatException ignored) {
            }
            log.logMessage("Use following command: §bcreate (name) (memory) (static) (version)", LogType.WARNING);
            return;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            var name = args[1];

            if (!groupManager.isServiceGroupExists(name)) {
                log.logMessage("This group does not exists", LogType.WARNING);
                return;
            }
            groupManager.removeServiceGroup(groupManager.getServiceGroupByNameOrNull(name));

            CloudAPI.getInstance().getServiceManager().getAllServicesByGroup(groupManager.getServiceGroupByNameOrNull(name))
                .forEach(it -> ((ServiceManager) CloudAPI.getInstance().getServiceManager()).shutdownService(it));

            log.logMessage("The group '§b" + name + "§7' is now deleted.");
            return;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            var name = args[1];

            if (!groupManager.isServiceGroupExists(name)) {
                log.logMessage("This group does not exists", LogType.WARNING);
                return;
            }

            var serviceGroup = groupManager.getServiceGroupByNameOrNull(name);
            log.logMessage("Group information's: ");
            log.logMessage("Groupname: §b" + serviceGroup.getGroup());
            log.logMessage("Template: §b" + serviceGroup.getTemplate());
            log.logMessage("Node: §b" + serviceGroup.getNode());
            log.logMessage("Memory: §b" + serviceGroup.getMemory() + "mb");
            log.logMessage("Min online services: §b" + serviceGroup.getMinOnlineService());
            log.logMessage("Max online services: §b" + serviceGroup.getMaxOnlineService());
            log.logMessage("Static service: §b" + serviceGroup.isStaticService());
            log.logMessage("Version: §b" + serviceGroup.getGameServerVersion().getTitle());
            return;
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("edit")) {
            final var name = args[1];

            if (!groupManager.isServiceGroupExists(name)) {
                log.logMessage("This group does not exists", LogType.WARNING);
                return;
            }

            final var serviceGroup = groupManager.getServiceGroupByNameOrNull(name);

            switch (args[2].toLowerCase()) {
                case "memory":
                    this.getAndSetInt("memory", args[3], serviceGroup.getName(), serviceGroup::setMemory);
                case "minservicecount":
                    this.getAndSetInt("minservicecount", args[3], serviceGroup.getName(), serviceGroup::setMinOnlineService);
                    break;
                case "maxservicecount":
                    this.getAndSetInt("maxservicecount", args[3], serviceGroup.getName(), serviceGroup::setMaxOnlineService);
                    break;
                case "static":
                    serviceGroup.setStatic(Boolean.parseBoolean(args[3]));
                    break;
                case "version":
                    serviceGroup.setGameVersion(GameServerVersion.valueOf(args[3]));
                    break;
            }
        }

        log.logMessage("§7Use following command: §bgroup list - List all groups");
        log.logMessage("§7Use following command: §bgroup create §7(§bname§7) (§bmemory§7) (§bstatic§7) (§bversion§7)");
        log.logMessage("§7Use following command: §bgroup remove §7(§bname§7)");
        log.logMessage("§7Use following command: §bgroup info §7(§bname§7)");
        log.logMessage("§7Use following command: §bgroup edit §7(§bname§7) (§bkey§7) (§bvalue§7)");
    }

    private void getAndSetInt(final String key, final String value, final String group, final Consumer<Integer> consumer) {
        try {
            consumer.accept(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            CloudAPI.getInstance().getLoggerProvider()
                .logMessage("§7Use following command: §bgroup edit " + group + " " + key + " §7(§bint§7)");
        }
    }

}
