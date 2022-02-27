package de.polocloud.base.node;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.service.CloudServiceRemoveEvent;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.network.packet.init.CacheInitPacket;
import de.polocloud.api.network.packet.service.ServiceRemovePacket;
import de.polocloud.api.service.ServiceState;
import de.polocloud.base.Base;
import de.polocloud.base.config.CloudConfiguration;
import de.polocloud.base.service.statistic.SimpleStatisticManager;
import de.polocloud.network.NetworkType;
import de.polocloud.network.server.NettyServer;
import de.polocloud.network.server.client.ConnectedClient;
import lombok.Getter;

import java.util.Objects;

@Getter
public final class BaseNode extends NettyServer {

    private final String hostName;
    private final int port;

    public BaseNode(final CloudConfiguration cloudConfiguration) {
        super(CloudAPI.getInstance().getPacketHandler(), cloudConfiguration.getNodeConfiguration().getNodeName(), NetworkType.NODE);

        this.hostName = cloudConfiguration.getNodeConfiguration().getHostname();
        this.port = cloudConfiguration.getNodeConfiguration().getPort();

        new BaseNodeNetwork();

        this.connect(this.hostName, this.port);
        Base.getInstance().getLogger().log("§7The node clustering is §asuccessfully §7started.");
    }

    @Override
    public void onNodeConnected(final ConnectedClient connectedClient) {
        Base.getInstance().getLogger()
            .log("§7The node '§b" + connectedClient.name() + "§7' §alogged §7into cluster.");
    }

    @Override
    public void onNodeDisconnected(final ConnectedClient connectedClient) {
        Base.getInstance().getLogger()
            .log("§7The node '§b" + connectedClient.name() + "§7' §cleaved §7the cluster.");
    }

    @Override
    public void onServiceConnected(final ConnectedClient connectedClient) {

        // set online
        final var service = Base.getInstance().getServiceManager().getServiceByNameOrNull(connectedClient.name());
        Objects.requireNonNull(service).setState(ServiceState.STARTED);

        // update cache
        connectedClient.sendPacket(new CacheInitPacket(
            Base.getInstance().getGroupManager().getAllCachedServiceGroups(),
            Base.getInstance().getServiceManager().getAllCachedServices(),
            Base.getInstance().getPlayerManager().getPlayers()));

        service.update();

        Base.getInstance().getLogger().log("§7The service '§b" + connectedClient.name() + "§7' has §asuccessfully §7connected to the cluster. (§b" + SimpleStatisticManager.getProcessingTime(service) + "ms)");
    }

    @Override
    public void onServiceDisconnected(final ConnectedClient client) {
        final var base = Base.getInstance();

        base.getServiceManager().getService(client.name())
            .ifPresentOrElse(service -> {
                base.getEventHandler().call(new CloudServiceRemoveEvent(service.getName()));
                base.getNode().sendPacketToAll(new ServiceRemovePacket(service.getName()));
                base.getServiceManager().getAllCachedServices().remove(service);
                base.getLogger().log("§7The service '§b" + service.getName() + "§7' has §cdisconnected§8.");
            }, () ->
                base.getLogger().log("§7Service §b" + client.name() + " §cdisconnected §7but doesn't exist!", LogType.WARNING));
    }

}
