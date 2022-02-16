package de.polocloud.network.cluster.impl;

import de.polocloud.network.cluster.INode;
import de.polocloud.network.cluster.impl.client.NodeClient;
import de.polocloud.network.cluster.type.NetworkType;
import de.polocloud.network.master.cache.IConnectedClient;
import de.polocloud.network.master.impl.Server;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.auth.NodeHandshakeAuthenticationPacket;
import de.polocloud.network.promise.CommunicationPromise;
import de.polocloud.network.promise.ICommunicationPromise;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractNodeClustering extends Server implements INode {

    private String nodeName;

    @Override
    public void addConnectedClient(Channel channel) {
        getAllCachedConnectedClients().add(new ClusteringConnectedClient(channel));
    }

    @Override
    public void authorize(IConnectedClient client, IPacket packet, ChannelHandlerContext context) {
        if (packet instanceof NodeHandshakeAuthenticationPacket authPacket) {
            client.setName(authPacket.getClientName());
            client.setAuthenticated(true);
            ((ClusteringConnectedClient) client).setNetworkType(authPacket.getType());
            this.onClientConnected(client);
        } else {
            context.close();
        }
    }

    @Override
    public void onClientConnected(IConnectedClient client) {
        if (client instanceof ClusteringConnectedClient connectedClient) {
            switch (connectedClient.getNetworkType()) {
                case NODE -> this.onNodeConnected(connectedClient);
                case SERVICE -> this.onServiceConnected(connectedClient);
            }
        }
    }

    @Override
    public void onClientDisconnected(IConnectedClient client) {
        if (client instanceof ClusteringConnectedClient connectedClient) {
            switch (connectedClient.getNetworkType()) {
                case NODE -> this.onNodeDisconnected(connectedClient);
                case SERVICE -> this.onServiceDisconnected(connectedClient);
            }
        }
    }

    public ICommunicationPromise<Void> connectToOtherNode(String name, String hostname, int port) {
        final var connectPromise = new CommunicationPromise<Void>();
        final var client = new NodeClient(name, NetworkType.NODE) {
            @Override
            public void onActivated(ChannelHandlerContext channelHandlerContext) {
                addConnectedClient(channelHandlerContext.channel());

                ClusteringConnectedClient connectedClient = (ClusteringConnectedClient)
                    getConnectedClientByChannel(channelHandlerContext.channel());
                connectedClient.setName(getClientName());
                connectedClient.setNetworkType(NetworkType.NODE);
            }

            @Override
            public void onClose(ChannelHandlerContext channelHandlerContext) {
                closeClient(channelHandlerContext);
            }
        };
        client.connectEstablishment(hostname, port).addListener(it -> {
            if (it.isSuccess()) {
                addConnectedClient((Channel) it.get());
                connectPromise.setSuccess(null);
            } else {
                connectPromise.setFailure(it.cause());
            }
        });
        return connectPromise;
    }

    public abstract void onNodeConnected(ClusteringConnectedClient connectedClient);

    public abstract void onNodeDisconnected(ClusteringConnectedClient connectedClient);

    public abstract void onServiceConnected(ClusteringConnectedClient connectedClient);

    public abstract void onServiceDisconnected(ClusteringConnectedClient connectedClient);

}
