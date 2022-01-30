package de.bytemc.cloud.node;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceStateUpdatePacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.config.NodeConfig;
import de.bytemc.cloud.services.statistics.SimpleStatisticManager;
import de.bytemc.network.cluster.impl.AbstractNodeClustering;
import de.bytemc.network.cluster.impl.ClusteringConnectedClient;
import lombok.Getter;

@Getter
public class BaseNode extends AbstractNodeClustering {

    private final String hostName;
    private final int port;

    public BaseNode(final NodeConfig nodeConfig) {
        super(nodeConfig.getNodeName());

        this.hostName = nodeConfig.getHostname();
        this.port = nodeConfig.getPort();

        new BaseNodeNetwork();

        connectEstablish(this.hostName, this.port).addResultListener(unused -> CloudAPI.getInstance().getLoggerProvider()
                .logMessage("§7The node clustering is§a successfully §7started."))
            .addFailureListener(Throwable::printStackTrace);
    }

    @Override
    public void onNodeConnected(final ClusteringConnectedClient clusteringConnectedClient) {
        CloudAPI.getInstance().getLoggerProvider()
            .logMessage("The node '§b" + clusteringConnectedClient.getName() + "§7' logged in the cluster.");
    }

    @Override
    public void onNodeDisconnected(final ClusteringConnectedClient clusteringConnectedClient) {
        CloudAPI.getInstance().getLoggerProvider()
            .logMessage("The node '§b" + clusteringConnectedClient.getName() + "§7' leaves in the cluster.");
    }

    @Override
    public void onServiceConnected(final ClusteringConnectedClient clusteringConnectedClient) {

        // set online
        final IService service = CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(clusteringConnectedClient.getName());
        service.setServiceState(ServiceState.ONLINE);

        // update cache
        clusteringConnectedClient.sendPacket(new ServiceGroupCacheUpdatePacket(CloudAPI.getInstance().getGroupManager().getAllCachedServiceGroups()));
        clusteringConnectedClient.sendPacket(new ServiceCacheUpdatePacket(CloudAPI.getInstance().getServiceManager().getAllCachedServices()));
        Base.getInstance().getNode().sendPacketToAll(new ServiceStateUpdatePacket(service.getName(), service.getServiceState()));

        CloudAPI.getInstance().getLoggerProvider().logMessage("The service '§b" + clusteringConnectedClient.getName() + "§7'§a successfully §7connect to the cluster. ("+ SimpleStatisticManager.getProcessingTime(service) + "ms)");
        Base.getInstance().getQueueService().checkForQueue();
    }

    @Override
    public void onServiceDisconnected(final ClusteringConnectedClient clusteringConnectedClient) {
        CloudAPI.getInstance().getLoggerProvider().logMessage("The service '§b" + clusteringConnectedClient.getName() + "§7' disconnect.");
    }
}
