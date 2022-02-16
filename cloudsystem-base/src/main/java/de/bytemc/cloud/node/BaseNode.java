package de.bytemc.cloud.node;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.events.events.CloudServiceRemoveEvent;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerCachePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceRemovePacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.config.NodeConfig;
import de.bytemc.cloud.services.LocalService;
import de.bytemc.cloud.services.statistics.SimpleStatisticManager;
import de.bytemc.network.cluster.impl.AbstractNodeClustering;
import de.bytemc.network.cluster.impl.ClusteringConnectedClient;
import lombok.Getter;

import java.util.Objects;

@Getter
public final class BaseNode extends AbstractNodeClustering {

    private final String hostName;
    private final int port;

    public BaseNode(final NodeConfig nodeConfig) {
        super(nodeConfig.getNodeName());

        this.hostName = nodeConfig.getHostname();
        this.port = nodeConfig.getPort();

        new BaseNodeNetwork();

        this.connectEstablish(this.hostName, this.port).addResultListener(unused -> Base.getInstance().getLoggerProvider()
                .logMessage("§7The node clustering is§a successfully §7started."))
            .addFailureListener(Throwable::printStackTrace);
    }

    @Override
    public void onNodeConnected(final ClusteringConnectedClient clusteringConnectedClient) {
        Base.getInstance().getLoggerProvider()
            .logMessage("The node '§b" + clusteringConnectedClient.getName() + "§7' logged in the cluster.");
    }

    @Override
    public void onNodeDisconnected(final ClusteringConnectedClient clusteringConnectedClient) {
        Base.getInstance().getLoggerProvider()
            .logMessage("The node '§b" + clusteringConnectedClient.getName() + "§7' leaves in the cluster.");
    }

    @Override
    public void onServiceConnected(final ClusteringConnectedClient clusteringConnectedClient) {

        // set online
        final IService service = Base.getInstance().getServiceManager().getServiceByNameOrNull(clusteringConnectedClient.getName());
        Objects.requireNonNull(service).setServiceState(ServiceState.ONLINE);

        // update cache
        clusteringConnectedClient.sendPacket(new ServiceGroupCacheUpdatePacket(Base.getInstance().getGroupManager().getAllCachedServiceGroups()));
        clusteringConnectedClient.sendPacket(new ServiceCacheUpdatePacket(Base.getInstance().getServiceManager().getAllCachedServices()));
        clusteringConnectedClient.sendPacket(new CloudPlayerCachePacket(Base.getInstance().getCloudPlayerManager().getAllCachedCloudPlayers()));

        service.update();

        Base.getInstance().getLoggerProvider().logMessage("The service '§b" + clusteringConnectedClient.getName() + "§7'§a successfully §7connect to the cluster. ("+ SimpleStatisticManager.getProcessingTime(service) + "ms)");
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
                base.getLoggerProvider().logMessage("The service '§b" + service.getName() + "§7' disconnect.");
                base.getQueueService().checkForQueue();
        }, () ->
                base.getLoggerProvider().logMessage("Service " + client.getName() + " disconnected but not exists!", LogType.WARNING));
    }
}
