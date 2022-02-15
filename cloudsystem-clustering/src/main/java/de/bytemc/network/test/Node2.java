package de.bytemc.network.test;

import de.bytemc.network.cluster.impl.client.NodeClient;
import de.bytemc.network.cluster.types.NetworkType;
import io.netty.channel.ChannelHandlerContext;

public class Node2 {

    public static void main(String[] args) {

        TestNode node2 = new TestNode("node-2");
        node2.connectEstablish("127.0.0.1", 8878).addListener(it -> {
            if (it.isSuccess()) {
                System.out.println("Node-2 » Successfully started node 2");
            } else {
                it.cause().printStackTrace();
            }
        });

        node2.connectToOtherNode("node-1", "127.0.0.1", 8876).addListener(it -> {
            if (it.isSuccess()) {
                System.out.println("Node-2 » Successfully connected to Node 1");
            } else {
                System.out.println("Node-2 » Node 1 is current offline.");
            }
        });


        NodeClient nodeClient = new NodeClient("lobby.1", NetworkType.SERVICE) {
            @Override
            public void onActivated(ChannelHandlerContext channelHandlerContext) {

            }

            @Override
            public void onClose(ChannelHandlerContext channelHandlerContext) {

            }
        };
        nodeClient.connectEstablishment("127.0.0.1", 8876);
    }

}
