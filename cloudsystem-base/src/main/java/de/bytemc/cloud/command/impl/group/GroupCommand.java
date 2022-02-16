package de.bytemc.cloud.command.impl.group;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.CloudCommand;
import de.bytemc.cloud.api.groups.DefaultGroup;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.versions.GameServerVersion;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class GroupCommand extends CloudCommand {



    public GroupCommand() {
        super("group", "Manage the cloud groups");
    }

    @Override
    public void execute(CloudAPI cloudAPI, String[] args) {


        final var groupManager = cloudAPI.getGroupManager();
        final var logger = cloudAPI.getLogger();


        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (final IServiceGroup serviceGroup : groupManager.getAllCachedServiceGroups()) {
                logger.logMessage("Name of group '§b" + serviceGroup.getName() + "§7' (§7Version '§b"
                    + serviceGroup.getGameServerVersion() + "§7' | Node: '" + serviceGroup.getNode() + "')");
            }
            return;
        } else if (args.length == 5 && args[0].equalsIgnoreCase("create")) {
            final var name = args[1];

            if (groupManager.isServiceGroupExists(name)) {
                logger.logMessage("This group is already exists", LogType.WARNING);
                return;
            }

            // create name memory
            try {
                final var memory = Integer.parseInt(args[2]);
                final var isStatic = Boolean.parseBoolean(args[3]);
                final var gameServerVersion = GameServerVersion.getVersionByName(args[4]);

                if (gameServerVersion == null) {
                    logger.logMessage("This version is not available.", LogType.WARNING);
                    logger.logMessage("Use one of the following versions:");
                    for (final var version : GameServerVersion.values()) {
                        logger.logMessage("- " + version.getName());
                    }
                    return;
                }

                final var serviceGroup = new DefaultGroup(name, memory, isStatic, gameServerVersion);
                groupManager.addServiceGroup(serviceGroup);
                serviceGroup.getGameServerVersion().download();

                Base.getInstance().getGroupTemplateService().createTemplateFolder(serviceGroup);
                logger.logMessage("The group '§b" + name + "§7' is now registered and online.");
                Base.getInstance().getQueueService().checkForQueue();
                return;
            } catch (NumberFormatException ignored) {}
            logger.logMessage("Use following command: §bcreate (name) (memory) (static) (version)", LogType.WARNING);
            return;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            final var name = args[1];
            final var serviceGroup = groupManager.getServiceGroupByNameOrNull(name);

            if (serviceGroup == null) {
                logger.logMessage("This group does not exists", LogType.WARNING);
                return;
            }
            groupManager.removeServiceGroup(serviceGroup);

            cloudAPI.getServiceManager().getAllServicesByGroup(serviceGroup).forEach(IService::stop);

            logger.logMessage("The group '§b" + name + "§7' is now deleted.");
            return;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            final var name = args[1];
            final var serviceGroup = groupManager.getServiceGroupByNameOrNull(name);

            if (serviceGroup == null) {
                logger.logMessage("This group does not exists", LogType.WARNING);
                return;
            }

            logger.logMessage("Group information's: ");
            logger.logMessage("Groupname: §b" + serviceGroup.getName());
            logger.logMessage("Template: §b" + serviceGroup.getTemplate());
            logger.logMessage("Node: §b" + serviceGroup.getNode());
            logger.logMessage("Memory: §b" + serviceGroup.getMemory() + "mb");
            logger.logMessage("Min online services: §b" + serviceGroup.getMinOnlineService());
            logger.logMessage("Max online services: §b" + serviceGroup.getMaxOnlineService());
            logger.logMessage("Static: §b" + serviceGroup.isStatic());
            logger.logMessage("Version: §b" + serviceGroup.getGameServerVersion().getTitle());
            logger.logMessage("Maintenance: §b" + serviceGroup.isMaintenance());
            return;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("edit")) {
            final var serviceGroup = groupManager.getServiceGroupByNameOrNull(args[1]);

            if (serviceGroup == null) {
                logger.logMessage("This group does not exists", LogType.WARNING);
                return;
            }

            final var key = args[2].toLowerCase();
            switch (key) {
                case "memory":
                    this.getAndSetInt(key, args[3], serviceGroup, serviceGroup::setMemory);
                    logger.logMessage("§7Successfully set memory to " + args[3] + "mb");
                case "minservicecount":
                    this.getAndSetInt(key, args[3], serviceGroup, serviceGroup::setMinOnlineService);
                    logger.logMessage("§7Successfully set min service count to " + args[3]);
                    return;
                case "maxservicecount":
                    this.getAndSetInt(key, args[3], serviceGroup, serviceGroup::setMaxOnlineService);
                    logger.logMessage("§7Successfully set max service count to " + args[3]);
                    return;
                case "defaultmaxplayers":
                    this.getAndSetInt(key, args[3], serviceGroup, serviceGroup::setDefaultMaxPlayers);
                    logger.logMessage("§7Successfully set default max players to " + args[3]);
                    return;
                case "fallback":
                    serviceGroup.setFallbackGroup(Boolean.parseBoolean(args[3]));
                    serviceGroup.update();
                    logger.logMessage("§7Successfully set fallback to " + args[3]);
                    return;
                case "maintenance":
                    serviceGroup.setMaintenance(Boolean.parseBoolean(args[3]));
                    serviceGroup.update();
                    logger.logMessage("§7Successfully set maintenance to " + args[3]);
                    return;
                case "version":
                    serviceGroup.setGameServerVersion(GameServerVersion.valueOf(args[3]));
                    serviceGroup.update();
                    logger.logMessage("§7Successfully set version to " + args[3]);
                    return;
            }
        }

        logger.logMessage("§7Use following command: §bgroup list - List all groups");
        logger.logMessage("§7Use following command: §bgroup create §7(§bname§7) (§bmemory§7) (§bstatic§7) (§bversion§7)");
        logger.logMessage("§7Use following command: §bgroup remove §7(§bname§7)");
        logger.logMessage("§7Use following command: §bgroup info §7(§bname§7)");
        logger.logMessage("§7Use following command: §bgroup edit §7(§bname§7) (§bkey§7) (§bvalue§7)");
    }

    @Override
    public List<String> tabComplete(String[] arguments) {
        if (arguments.length == 1) {
            return Arrays.asList("list", "create", "remove", "info", "edit");
        } else if (arguments.length == 2) {
            if (!arguments[0].equalsIgnoreCase("list")) {
                return Base.getInstance().getGroupManager().getAllCachedServiceGroups().stream().map(IServiceGroup::getName).toList();
            }
        } else if (arguments.length == 3) {
            if (arguments[0].equalsIgnoreCase("edit")) {
                return Arrays.asList("memory", "minservicecount", "maxservicecount",
                    "defaultmaxplayers", "fallback", "maintenance", "version");
            }
        } else if (arguments.length == 4) {
            if (arguments[0].equalsIgnoreCase("create")) {
                return Arrays.asList("true", "false");
            }
        } else if (arguments.length == 5) {
            if (arguments[0].equalsIgnoreCase("create")) {
                return Arrays.stream(GameServerVersion.values()).map(GameServerVersion::getName).toList();
            }
        }
        return super.tabComplete(arguments);
    }

    private void getAndSetInt(final String key, final String value, final IServiceGroup group, final Consumer<Integer> consumer) {
        try {
            consumer.accept(Integer.parseInt(value));
            group.update();
        } catch (NumberFormatException e) {
            CloudAPI.getInstance().getLogger()
                .logMessage("§7Use following command: §bgroup edit " + group.getName() + " " + key + " §7(§bint§7)");
        }
    }

}
