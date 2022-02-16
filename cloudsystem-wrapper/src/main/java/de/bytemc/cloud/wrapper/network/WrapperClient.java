package de.bytemc.cloud.wrapper.network;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.network.cluster.impl.client.NodeClient;
import de.bytemc.network.cluster.types.NetworkType;
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
