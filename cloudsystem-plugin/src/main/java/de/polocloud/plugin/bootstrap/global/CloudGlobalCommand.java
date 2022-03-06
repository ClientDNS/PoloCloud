package de.polocloud.plugin.bootstrap.global;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.service.CloudService;
import io.netty.util.internal.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CloudGlobalCommand {

    public static void execute(final @NotNull PlayerMessageObject source, final String[] arguments) {

        if (!source.hasPermission("cloud.network.command")) {
            source.sendMessage("§cYou have no permissions for this command.");
            return;
        }

        final var serviceManager = CloudAPI.getInstance().getServiceManager();
        final var groupManager = CloudAPI.getInstance().getGroupManager();

        if (arguments.length == 2) {
            if (arguments[0].equalsIgnoreCase("shutdown")) {
                serviceManager.getService(arguments[1]).ifPresentOrElse(cloudService -> {
                    cloudService.stop();
                    source.sendMessage("§7You stop the service §8'§b" + cloudService.getName() + "§8'");
                }, () -> groupManager.getServiceGroupByName(arguments[1]).ifPresentOrElse(group -> {
                    List<CloudService> allServicesByGroup = serviceManager.getAllServicesByGroup(group);
                    source.sendMessage("§7The service(s) §b" + String.join(", ",
                        allServicesByGroup.stream().map(CloudService::getName).toList()) + " §7trying to shutdown.");
                    allServicesByGroup.forEach(CloudService::stop);
                }, () -> source.sendMessage("§cThis group or service does not exists.")));
                return;
            }
            if (arguments[0].equalsIgnoreCase("info")) {
                serviceManager.getService(arguments[1]).ifPresentOrElse(cloudService -> {
                    source.sendMessage("§8› §7All information about the service: §f" + cloudService.getName());
                    source.sendMessage("§8● §7Service state: §b" + cloudService.getState());
                    source.sendMessage("§8● §7Motd: §b" + cloudService.getMotd());
                    source.sendMessage("§8● §7Players: §8(§b" + cloudService.getOnlineCount() + "§8/§b" + cloudService.getMaxPlayers() + "§8)");
                    source.sendMessage("§8● §7Servie node: §b" + cloudService.getNode());
                    source.sendMessage("§8● §7Port: §b" + cloudService.getPort());
                }, () -> groupManager.getServiceGroupByName(arguments[1]).ifPresentOrElse(group -> {
                    source.sendMessage("§8› §7All information about the group: §f" + group.getName());
                    source.sendMessage("§8● §7Memory: §b" + group.getMaxMemory());
                    source.sendMessage("§8● §7Version: §b" + group.getGameServerVersion().getName());
                    source.sendMessage("§8● §7Default max players: §b" + group.getDefaultMaxPlayers());
                    source.sendMessage("§8● §7min online service: §b" + group.getMinOnlineService());
                    source.sendMessage("§8● §7Max online service: §b" + group.getMaxOnlineService());
                    source.sendMessage("§8● §7Node(s): §b" + group.getNode());
                }, () -> source.sendMessage("§cThis group or service does not exists.")));
                return;
            }
        }
        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("list")) {
            final var nodeServices = new HashMap<String, List<CloudService>>();
            for (final var allCachedService : serviceManager.getAllCachedServices()) {
                final var current = nodeServices.getOrDefault(allCachedService.getNode(), Lists.newArrayList());
                current.add(allCachedService);
                nodeServices.put(allCachedService.getNode(), current);
            }
            nodeServices.keySet().forEach(it -> {
                final var services = nodeServices.get(it).stream().filter(s -> s.getGroup().getGameServerVersion().isProxy()).toList();
                source.sendMessage("§8› §7" + it + "§8: (§7Proxies: §c" + services.size() + " Services §8┃ §f"
                    + services.stream().mapToInt(CloudService::getOnlineCount).sum() + " Players§8)");
                services.forEach(ser -> source.sendMessage("§8● §f" + ser.getName()
                    + "§8 (§b" + ser.getOnlineCount() + "§8/§b" + ser.getMaxPlayers() + " §8┃ §b" + ser.getState() + "§8)"));
                source.sendMessage(StringUtil.EMPTY_STRING);

                var server = nodeServices.get(it).stream().filter(s -> !s.getGroup().getGameServerVersion().isProxy()).toList();
                source.sendMessage("§8› §7" + it + "§8: (§7Server: §c" + server.size() + " Services §8┃ §f"
                    + server.stream().mapToInt(CloudService::getOnlineCount).sum() + " Players§8)");
                server.forEach(ser -> source.sendMessage("§8● §f" + ser.getName()
                    + "§8 (§b" + ser.getOnlineCount() + "§8/§b" + ser.getMaxPlayers() + " §8┃ §b" + ser.getState() + "§8)"));
                source.sendMessage(StringUtil.EMPTY_STRING);
            });
            return;
        }
        source.sendMessage("§8› §bcloud list §8- §7List all cloud services of every node.");
        source.sendMessage("§8› §bcloud shutdown (service/group) §8- §7Stop a current component.");
        source.sendMessage("§8› §bcloud info (service/group) §8- §7Information about a component.");
    }

    public static List<String> tabComplete(String[] args, Function<String, Boolean> permissions){
        if(args.length == 0 || args.length > 2 || !permissions.apply("cloud.network.command")) return ImmutableList.of();
        List<String> matches = new ArrayList<>();

        if(args.length == 1) {
            matches.add("list");
            matches.add("info");
            matches.add("shutdown");
        }
        if(args[0].equalsIgnoreCase("shutdown") || args[0].equalsIgnoreCase("info")) {
            CloudAPI.getInstance().getGroupManager().getAllCachedServiceGroups().stream().map(it -> it.getName()).toList().forEach(it -> matches.add(it));
            CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream().map(it -> it.getName()).toList().forEach(it -> matches.add(it));
        }
        return matches;
    }

}
