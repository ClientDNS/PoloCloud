package de.bytemc.cloud.node;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceCacheUpdatePacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.config.NodeConfig;
import de.bytemc.network.cluster.impl.AbstractNodeClustering;
import de.bytemc.network.cluster.impl.ClusteringConnectedClient;
import lombok.Getter;

@Getter
public class BaseNode extends AbstractNodeClustering {

    private String hostname;
    private int port;

    public BaseNode(NodeConfig nodeConfig) {
        super(nodeConfig.getNodeName());

        this.hostname = nodeConfig.getHostname();
        this.port = nodeConfig.getPort();

        new BaseNodeNetwork();

        connectEstablish(hostname, port).addResultListener(unused -> CloudAPI.getInstance().getLoggerProvider().logMessage("§7The node clustering is§a successfully §7started."))
            .addFailureListener(throwable -> throwable.printStackTrace());
    }

    @Override
    public void onNodeConnected(ClusteringConnectedClient clusteringConnectedClient) {
        CloudAPI.getInstance().getLoggerProvider().logMessage("The node '§b" + clusteringConnectedClient.getName() + "§7' logged in the cluster.");
    }

    @Override
    public void onNodeDisconnected(ClusteringConnectedClient clusteringConnectedClient) {
        CloudAPI.getInstance().getLoggerProvider().logMessage("The node '§b" + clusteringConnectedClient.getName() + "§7' leaves in the cluster.");
    }

    @Override
    public void onServiceConnected(ClusteringConnectedClient clusteringConnectedClient) {
        //set online
        IService service = CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(clusteringConnectedClient.getName());
        service.setServiceState(ServiceState.ONLINE);

        //update cache
        clusteringConnectedClient.sendPacket(new ServiceGroupCacheUpdatePacket(CloudAPI.getInstance().getGroupManager().getAllCachedServiceGroups()));
        clusteringConnectedClient.sendPacket(new ServiceCacheUpdatePacket(CloudAPI.getInstance().getServiceManager().getAllCachedServices()));

        CloudAPI.getInstance().getLoggerProvider().logMessage("The service '§b" + clusteringConnectedClient.getName() + "§7' connect to the cluster. (" +
            service.getServiceState().getName() + "§7)");
        Base.getInstance().getQueueService().checkForQueue();
    }

    @Override
    public void onServiceDisconnected(ClusteringConnectedClient clusteringConnectedClient) {
        CloudAPI.getInstance().getLoggerProvider().logMessage("The service '§b" + clusteringConnectedClient.getName() + "§7' disconnect.");
    }
}
