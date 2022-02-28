package de.polocloud.base.command.defaults;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.version.GameServerVersion;
import de.polocloud.base.Base;
import de.polocloud.base.command.CloudCommand;
import de.polocloud.base.group.DefaultGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@CloudCommand.Command(name = "group", description = "Manage the cloud groups")
public final class GroupCommand extends CloudCommand {

    @Override
    public void execute(Base base, String[] args) {
        final var groupManager = base.getGroupManager();
        final var logger = base.getLogger();


        if (args.length == 1 && args[0].equals("list")) {
            for (final var serviceGroup : groupManager.getAllCachedServiceGroups()) {
                logger.log("§7Name of group '§b" + serviceGroup.getName() + "§7' (§7Version '§b"
                    + serviceGroup.getGameServerVersion() + "§7' | Node: '" + serviceGroup.getNode() + "')");
            }
            return;
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("info")) {
                groupManager.getServiceGroupByName(args[0]).ifPresentOrElse(group ->
                    logger.log("Group information's: ",
                        "Group: §b" + group.getName(),
                        "Template: §b" + group.getTemplate(),
                        "Node: §b" + group.getNode(),
                        "Max Memory: §b" + group.getMaxMemory() + "mb",
                        "Min online services: §b" + group.getMinOnlineService(),
                        "Max online services: §b" + group.getMaxOnlineService(),
                        "Static: §b" + group.isStatic(),
                        "Version: §b" + group.getGameServerVersion().getName(),
                        "Maintenance: §b" + group.isMaintenance()), () -> logger.log("This group does not exists", LogType.WARNING));
                return;
            } else if (args[1].equalsIgnoreCase("remove")) {
                groupManager.getServiceGroupByName(args[0]).ifPresentOrElse(group -> {
                    groupManager.removeServiceGroup(group);
                    base.getServiceManager().getAllServicesByGroup(group).forEach(CloudService::stop);
                    logger.log("§7The group '§b" + group.getName() + "§7' is now §cdeleted§7.");
                }, () -> logger.log("§cThis group does not exists", LogType.WARNING));
                return;
            }
        } else if (args.length == 4 && args[1].equalsIgnoreCase("edit")) {
            groupManager.getServiceGroupByName(args[0]).ifPresentOrElse(group -> {
                final var key = args[2].toLowerCase();
                switch (key) {
                    case "memory":
                        this.getAndSetInt(key, args[3], group, integer -> {
                            group.setMaxMemory(integer);
                            base.getDatabaseManager().getProvider()
                                .updateGroupProperty(group.getName(), "maxMemory", integer);
                        });
                        logger.log("§7Successfully set memory to " + args[3] + "mb");
                        return;
                    case "minservicecount":
                        this.getAndSetInt(key, args[3], group, integer -> {
                            group.setMinOnlineService(integer);
                            base.getDatabaseManager().getProvider()
                                .updateGroupProperty(group.getName(), "minOnlineService", integer);
                        });
                        logger.log("§7Successfully set min service count to " + args[3]);
                        return;
                    case "maxservicecount":
                        this.getAndSetInt(key, args[3], group, integer -> {
                            group.setMaxOnlineService(integer);
                            base.getDatabaseManager().getProvider()
                                .updateGroupProperty(group.getName(), "maxOnlineService", integer);
                        });
                        logger.log("§7Successfully set max service count to " + args[3]);
                        return;
                    case "defaultmaxplayers":
                        this.getAndSetInt(key, args[3], group, integer -> {
                            group.setDefaultMaxPlayers(integer);
                            base.getDatabaseManager().getProvider()
                                .updateGroupProperty(group.getName(), "maxPlayers", integer);
                        });
                        logger.log("§7Successfully set default max players to " + args[3]);
                        return;
                    case "fallback":
                        final var fallback = args[3].toLowerCase();
                        if (!fallback.equals("true") && !fallback.equals("false")) {
                            logger.log("§7Please use §btrue§7/§bfalse");
                            return;
                        }
                        group.setFallbackGroup(Boolean.parseBoolean(fallback));
                        group.update();
                        base.getDatabaseManager().getProvider()
                            .updateGroupProperty(group.getName(), "fallback", (fallback.equals("true") ? 1 : 0));
                        logger.log("§7Successfully set fallback to " + fallback);
                        return;
                    case "maintenance":
                        final var maintenance = args[3].toLowerCase();

                        if (!maintenance.equals("true") && !maintenance.equals("false")) {
                            logger.log("Please use true/false");
                            return;
                        }
                        group.setMaintenance(Boolean.parseBoolean(maintenance));
                        group.update();
                        base.getDatabaseManager().getProvider()
                            .updateGroupProperty(group.getName(), "maintenance", (maintenance.equals("true") ? 1 : 0));
                        logger.log("§7Successfully set maintenance to " + maintenance);
                        return;
                    case "version":
                        final var gameServerVersion = GameServerVersion.getVersionByName(args[3]);

                        if (gameServerVersion == null) {
                            logger.log("§7This version is §cnot §7available.", LogType.WARNING);
                            logger.log("§7Please use one of the following versions:");
                            for (final var version : GameServerVersion.values()) logger.log("- " + version.getName());
                            return;
                        }
                        group.setGameServerVersion(gameServerVersion);
                        group.update();
                        logger.log("§7Successfully set version to " + args[3]);
                }
            }, () -> logger.log("This group does not exists", LogType.WARNING));
            return;
        } else if (args.length == 5 && args[0].equalsIgnoreCase("create")) {
            final var name = args[1];

            if (groupManager.isServiceGroupExists(name)) {
                logger.log("This group is already exists", LogType.WARNING);
                return;
            }

            // create name memory
            try {
                final var memory = Integer.parseInt(args[2]);
                final var isStatic = Boolean.parseBoolean(args[3]);
                final var gameServerVersion = GameServerVersion.getVersionByName(args[4]);

                if (gameServerVersion == null) {
                    logger.log("This version is not available.", LogType.WARNING);
                    logger.log("Use one of the following versions:");
                    for (final var version : GameServerVersion.values()) logger.log("- " + version.getName());
                    return;
                }

                final var serviceGroup = new DefaultGroup(base.getNode().getName(), name, memory, isStatic, gameServerVersion);
                groupManager.addServiceGroup(serviceGroup);
                serviceGroup.getGameServerVersion().download(serviceGroup.getTemplate());

                base.getGroupTemplateService().createTemplateFolder(serviceGroup);
                logger.log("The group '§b" + name + "§7' is now registered and online.");
                return;
            } catch (NumberFormatException ignored) {
            }
            logger.log("Use following command: §bcreate (name) (memory) (static) (version)", LogType.WARNING);
            return;
        }
        final var help = "§7Use following command: §b";
        logger.log(
            help + "group list §7- List all available groups.",
            help + "group (name) remove §7- Removes group and stops server of that group.",
            help + "group (name) info §7- Shows information about this group.",
            help + "group (name) edit (key) (value) §7- Edits a group (press TAB on the key to see all options).",
            help + "group create (name) (memory) (static <true/false>) (version) §7- Creates a new group.");
    }

    @Override
    public List<String> tabComplete(String[] arguments) {
        if (arguments.length == 1) {
            List<String> answers = new ArrayList<>(Base.getInstance().getGroupManager().getAllCachedServiceGroups().stream().map(ServiceGroup::getName).toList());
            answers.add("list");
            answers.add("create");
            return answers;
        } else if (arguments.length == 2) {
            if (!arguments[0].equalsIgnoreCase("list") && !arguments[0].equalsIgnoreCase("create")) {
                return Arrays.asList("remove", "info", "edit");
            }
        } else if (arguments.length == 3) {
            if (arguments[1].equalsIgnoreCase("edit")) {
                return Arrays.asList("memory", "minServiceCount", "maxServiceCount",
                    "defaultMaxPlayers", "fallback", "maintenance", "version");
            }
        } else if (arguments.length == 4) {
            if (arguments[0].equalsIgnoreCase("create")) {
                return Arrays.asList("true", "false");
            } else if (arguments[1].equalsIgnoreCase("edit")) {
                if (arguments[2].equalsIgnoreCase("fallback") || arguments[2].equalsIgnoreCase("maintenance")) {
                    return Arrays.asList("true", "false");
                } else if (arguments[2].equalsIgnoreCase("version")) {
                    return Arrays.stream(GameServerVersion.values()).map(GameServerVersion::getName).toList();
                }
            }
        } else if (arguments.length == 5) {
            if (arguments[0].equalsIgnoreCase("create")) {
                return Arrays.stream(GameServerVersion.values()).map(GameServerVersion::getName).toList();
            }
        }
        return super.tabComplete(arguments);
    }

    private void getAndSetInt(final String key, final String value, final ServiceGroup group, final Consumer<Integer> consumer) {
        try {
            consumer.accept(Integer.parseInt(value));
            group.update();
        } catch (NumberFormatException e) {
            CloudAPI.getInstance().getLogger()
                .log("§7Use following command: §bgroup edit " + group.getName() + " " + key + " §7(§bint§7)");
        }
    }

}
