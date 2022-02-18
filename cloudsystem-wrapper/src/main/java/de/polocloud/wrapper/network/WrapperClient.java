package de.polocloud.wrapper.network;

import de.polocloud.api.CloudAPI;
import de.polocloud.network.NetworkType;
import de.polocloud.network.client.NettyClient;
import de.polocloud.network.packet.PacketHandler;
import io.netty.channel.ChannelHandlerContext;

public final class WrapperClient extends NettyClient {

    public WrapperClient(final PacketHandler packetHandler, final String name, final String hostname, final int port) {
        super(packetHandler, name, NetworkType.WRAPPER);

        this.connect(hostname, port);
        CloudAPI.getInstance().getLogger().log("The service start successfully network service.");
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
