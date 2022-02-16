package de.polocloud.base.node;

import de.polocloud.base.Base;
import de.polocloud.api.event.service.CloudServiceRemoveEvent;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.network.packet.group.ServiceGroupCacheUpdatePacket;
import de.polocloud.api.network.packet.player.CloudPlayerCachePacket;
import de.polocloud.api.network.packet.service.ServiceCacheUpdatePacket;
import de.polocloud.api.network.packet.service.ServiceRemovePacket;
import de.polocloud.api.service.IService;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.base.config.CloudConfiguration;
import de.polocloud.base.service.LocalService;
import de.polocloud.base.service.statistic.SimpleStatisticManager;
import de.polocloud.network.cluster.impl.AbstractNodeClustering;
import de.polocloud.network.cluster.impl.ClusteringConnectedClient;
import lombok.Getter;

import java.util.Objects;

@Getter
public final class BaseNode extends AbstractNodeClustering {

    private final String hostName;
    private final int port;

    public BaseNode(final CloudConfiguration cloudConfiguration) {
        super(cloudConfiguration.getNodeConfiguration().getNodeName());

        this.hostName = cloudConfiguration.getNodeConfiguration().getHostname();
        this.port = cloudConfiguration.getNodeConfiguration().getPort();

        new BaseNodeNetwork();

        this.connectEstablish(this.hostName, this.port).addResultListener(unused -> Base.getInstance().getLogger()
                .log("§7The node clustering is§a successfully §7started."))
            .addFailureListener(Throwable::printStackTrace);
    }

    @Override
    public void onNodeConnected(final ClusteringConnectedClient clusteringConnectedClient) {
        Base.getInstance().getLogger()
            .log("The node '§b" + clusteringConnectedClient.getName() + "§7' logged in the cluster.");
    }

    @Override
    public void onNodeDisconnected(final ClusteringConnectedClient clusteringConnectedClient) {
        Base.getInstance().getLogger()
            .log("The node '§b" + clusteringConnectedClient.getName() + "§7' leaves in the cluster.");
    }

    @Override
    public void onServiceConnected(final ClusteringConnectedClient clusteringConnectedClient) {

        // set online
        final IService service = Base.getInstance().getServiceManager().getServiceByNameOrNull(clusteringConnectedClient.getName());
        Objects.requireNonNull(service).setServiceState(ServiceState.ONLINE);

        // update cache
        clusteringConnectedClient.sendPacket(new ServiceGroupCacheUpdatePacket(Base.getInstance().getGroupManager().getAllCachedServiceGroups()));
        clusteringConnectedClient.sendPacket(new ServiceCacheUpdatePacket(Base.getInstance().getServiceManager().getAllCachedServices()));
        clusteringConnectedClient.sendPacket(new CloudPlayerCachePacket(Base.getInstance().getPlayerManager().getPlayers()));

        service.update();

        Base.getInstance().getLogger().log("The service '§b" + clusteringConnectedClient.getName() + "§7'§a successfully §7connect to the cluster. ("+ SimpleStatisticManager.getProcessingTime(service) + "ms)");
        Base.getInstance().getQueueService().checkForQueue();
    }

    @Override
    public void onServiceDisconnected(final ClusteringConnectedClient client) {
        final var base = Base.getInstance();

        base.getServiceManager().getService(client.getName())
            .ifPresentOrElse(service -> {
                ((LocalService) service).delete();
                base.getEventHandler().call(new CloudServiceRemoveEvent(service.getName()));
                base.getNode().sendPacketToAll(new ServiceRemovePacket(service.getName()));
                base.getServiceManager().getAllCachedServices().remove(service);
                base.getLogger().log("The service '§b" + service.getName() + "§7' disconnect.");
                base.getQueueService().checkForQueue();
        }, () ->
                base.getLogger().log("Service " + client.getName() + " disconnected but not exists!", LogType.WARNING));
    }
}
