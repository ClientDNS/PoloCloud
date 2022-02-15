package de.bytemc.network.cluster.impl;

import de.bytemc.network.cluster.types.NetworkType;
import de.bytemc.network.master.cache.SimpleConnectedClient;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClusteringConnectedClient extends SimpleConnectedClient {

    private NetworkType networkType = NetworkType.UNKNOWN;

    public ClusteringConnectedClient(Channel channel) {
        super(channel);
    }
}
