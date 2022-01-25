package de.bytemc.cloud.node;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.network.cluster.impl.AbstractNodeClustering;
import de.bytemc.network.cluster.impl.ClusteringConnectedClient;

public class BaseNode extends AbstractNodeClustering {


    public BaseNode() {
        //TODO place in configuration
        super("node-1");
        connectEstablish("127.0.0.1", 9943)
            .addResultListener(unused -> CloudAPI.getInstance().getLoggerProvider().logMessage("§7The node clustering is§a successfully §7started."))
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


    }

    @Override
    public void onServiceDisconnected(ClusteringConnectedClient clusteringConnectedClient) {

    }
}
