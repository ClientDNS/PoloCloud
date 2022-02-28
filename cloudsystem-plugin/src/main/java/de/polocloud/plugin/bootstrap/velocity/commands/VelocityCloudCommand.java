package de.polocloud.plugin.bootstrap.velocity.commands;

import com.google.common.collect.Lists;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.service.CloudService;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.List;

public final class VelocityCloudCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {

        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 2 && args[0].equalsIgnoreCase("shutdown")) {
            CloudAPI.getInstance().getServiceManager().getService(args[1]).ifPresentOrElse(cloudService -> {
                    cloudService.stop();
                    source.sendMessage(Component.text("§7You stop the service §8'§b" + cloudService.getName() + "§8'"));
                },
                () -> source.sendMessage(Component.text("§cThis service does not exists.")));
            return;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("stop")) {
            CloudAPI.getInstance().getGroupManager().getServiceGroupByName(args[1]).ifPresentOrElse(group -> {
                    List<CloudService> allServicesByGroup = CloudAPI.getInstance().getServiceManager().getAllServicesByGroup(group);
                    source.sendMessage(Component.text("§7The service(s) §b" + String.join(", ",
                        allServicesByGroup.stream().map(it -> it.getName()).toList()) + " §7trying to shutdown."));
                    allServicesByGroup.forEach(it -> it.stop());
                },
                () -> source.sendMessage(Component.text("§cThis group does not exists.")));
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

            var nodeServices = new HashMap<String, List<CloudService>>();
            for (var allCachedService : CloudAPI.getInstance().getServiceManager().getAllCachedServices()) {
                var current = nodeServices.getOrDefault(allCachedService.getNode(), Lists.newArrayList());
                current.add(allCachedService);
                nodeServices.put(allCachedService.getNode(), current);
            }
            nodeServices.keySet().forEach(it -> {
                //proxies
                var services = nodeServices.get(it).stream().filter(s -> s.getGroup().getGameServerVersion().isProxy()).toList();
                source.sendMessage(Component.text("§8› §7" + it + "§8: (§7Proxies: §c" + services.size() + " Services §8┃ §f" + services.stream().mapToInt(t -> t.getOnlineCount()).sum() + " Players§8)"));
                services.forEach(ser -> source.sendMessage(Component.text("§8● §f" + ser.getName() + "§8 (§e" + ser.getOnlineCount() + "§8/§e" + ser.getMaxPlayers() + " §8┃ §b" + ser.getState() + "§8)")));
                source.sendMessage(Component.text(" "));
                //services
                var server = nodeServices.get(it).stream().filter(s -> !s.getGroup().getGameServerVersion().isProxy()).toList();
                source.sendMessage(Component.text("§8› §7" + it + "§8: (§7Server: §c" + server.size() + " Services §8┃ §f" + server.stream().mapToInt(t -> t.getOnlineCount()).sum() + " Players§8)"));
                server.forEach(ser -> source.sendMessage(Component.text("§8● §f" + ser.getName() + "§8 (§e" + ser.getOnlineCount() + "§8/§e" + ser.getMaxPlayers() + " §8┃ §b" + ser.getState() + "§8)")));
                source.sendMessage(Component.text(" "));
            });
            return;
        }
        source.sendMessage(Component.text("§8› §bcloud list §8- §7List all cloud services of every node."));
        source.sendMessage(Component.text("§8› §bcloud stop (group) §8- §7Stop all services by group."));
        source.sendMessage(Component.text("§8› §bcloud shutdown (service) §8- §7Stop a current service."));
    }
}
