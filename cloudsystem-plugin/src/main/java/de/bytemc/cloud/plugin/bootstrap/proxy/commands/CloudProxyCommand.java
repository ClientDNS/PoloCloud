package de.bytemc.cloud.plugin.bootstrap.proxy.commands;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.services.ServiceRequestShutdownPacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.wrapper.Wrapper;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public final class CloudProxyCommand extends Command {

    public CloudProxyCommand() {
        super("cloud");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("ks")) {
            final IService service = CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(args[1]);

            if (service == null) {
                sender.sendMessage(new TextComponent("§cDieser service existiert nicht."));
                return;
            }

            if (service.getServiceState() != ServiceState.ONLINE) {
                sender.sendMessage(new TextComponent("§cDieser ist noch nicht online."));
                return;
            }

            sender.sendMessage(new TextComponent("§cService " + service.getName() + " is trying to stop."));
            Wrapper.getInstance().getClient().sendPacket(new ServiceRequestShutdownPacket(service.getName()));
        }
    }

}
