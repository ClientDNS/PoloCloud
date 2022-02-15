package de.bytemc.network.test;

public class Node1 {

    public static void main(String[] args) {
        TestNode node = new TestNode("node-1");
        node.connectEstablish("127.0.0.1", 8876).addListener(it -> {
            if (it.isSuccess()) {
                System.out.println("Node-1 » Successfully server started");
            } else {
                it.cause().printStackTrace();
            }
        });

        node.connectToOtherNode("node-2", "127.0.0.1", 8878).addListener(it -> {
            if (it.isSuccess()) {
                System.out.println("Node-1 » Successfully connected to Node 2");
            } else {
                System.out.println("Node-1 » Node 2 is current offline.");
            }
        });

    }

}
