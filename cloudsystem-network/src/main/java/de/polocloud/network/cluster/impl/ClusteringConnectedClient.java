package de.polocloud.network.cluster.impl;

import de.polocloud.network.cluster.type.NetworkType;
import de.polocloud.network.master.cache.SimpleConnectedClient;
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
