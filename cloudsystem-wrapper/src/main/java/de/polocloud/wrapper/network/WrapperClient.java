package de.polocloud.wrapper.network;

import de.polocloud.api.CloudAPI;
import de.polocloud.network.cluster.impl.client.NodeClient;
import de.polocloud.network.cluster.type.NetworkType;
import io.netty.channel.ChannelHandlerContext;

public final class WrapperClient extends NodeClient {

    public WrapperClient(final String clientName, final String hostname, final int port) {
        super(clientName, NetworkType.SERVICE);

        this.connectEstablishment(hostname, port).addResultListener(it ->
            CloudAPI.getInstance().getLogger().log("The service start successfully network service."))
            .addFailureListener(Throwable::printStackTrace);
    }

    @Override
    public void onActivated(ChannelHandlerContext channelHandlerContext) {
        CloudAPI.getInstance().getLogger().log("This service successfully connected to the cluster.");
    }

    @Override
    public void onClose(ChannelHandlerContext channelHandlerContext) {
        CloudAPI.getInstance().getLogger().log("This service disconnected from the cluster");
    }

}
