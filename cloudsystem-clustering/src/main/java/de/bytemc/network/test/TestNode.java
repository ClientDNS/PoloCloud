package de.bytemc.network.test;

import de.bytemc.network.cluster.impl.AbstractNodeClustering;
import de.bytemc.network.cluster.impl.ClusteringConnectedClient;

public class TestNode extends AbstractNodeClustering {

    public TestNode(String nodeName) {
        super(nodeName);
    }

    @Override
    public void onNodeConnected(ClusteringConnectedClient client) {
        System.out.println(getNodeName() + " » The node '" + client.getName() + "' connect to the cluster.");
    }

    @Override
    public void onNodeDisconnected(ClusteringConnectedClient connectedClient) {
        System.out.println(getNodeName() + " » The node '" + connectedClient.getName() + "' disconnected");
    }

    @Override
    public void onServiceConnected(ClusteringConnectedClient connectedClient) {
        System.out.println(getNodeName() + " » The service '" + connectedClient.getName() + "' connected.");
    }

    @Override
    public void onServiceDisconnected(ClusteringConnectedClient connectedClient) {
        System.out.println(getNodeName() + " » The service '" + connectedClient.getName() + "' disconnected.");
    }

}
