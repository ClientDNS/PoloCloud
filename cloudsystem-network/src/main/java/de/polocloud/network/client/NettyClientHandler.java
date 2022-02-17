package de.polocloud.network.client;

import de.polocloud.network.NetworkType;
import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.auth.NodeHandshakeAuthenticationPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public final class NettyClientHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet Packet) {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new NodeHandshakeAuthenticationPacket("Lobby", NetworkType.WRAPPER));
    }
}
