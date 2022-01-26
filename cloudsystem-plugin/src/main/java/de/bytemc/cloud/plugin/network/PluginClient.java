package de.bytemc.cloud.plugin.network;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.network.cluster.impl.client.NodeClient;
import de.bytemc.network.cluster.types.NetworkType;
import io.netty.channel.ChannelHandlerContext;

public class PluginClient extends NodeClient {

    public PluginClient(String clientName, String hostname, int port) {
        super(clientName, NetworkType.SERVICE);

        connectEstablishment(hostname, port).addResultListener(it -> {
            CloudAPI.getInstance().getLoggerProvider().logMessage("The service start successfully network service.");
        }).addFailureListener(it -> it.printStackTrace());
    }

    @Override
    public void onActivated(ChannelHandlerContext channelHandlerContext) {
        CloudAPI.getInstance().getLoggerProvider().logMessage("This service successfully connected to the cluster.");
    }

    @Override
    public void onClose(ChannelHandlerContext channelHandlerContext) {
        CloudAPI.getInstance().getLoggerProvider().logMessage("This service disconnected from the cluster");
    }
}
