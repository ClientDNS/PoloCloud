package de.polocloud.network.client;

import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.auth.NodeHandshakeAuthenticationPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public final class NettyClientHandler extends SimpleChannelInboundHandler<Packet> {

    private final NettyClient nettyClient;

    public NettyClientHandler(final NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        this.nettyClient.getPacketHandler().call(channelHandlerContext, packet);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new NodeHandshakeAuthenticationPacket(this.nettyClient.getName(), this.nettyClient.getNetworkType()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}
