package de.polocloud.network.codec.impl;

import de.polocloud.network.NetworkManager;
import de.polocloud.network.packet.IPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SimplePacketAbstractHandler extends SimpleChannelInboundHandler<IPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IPacket packet) {
        NetworkManager.callPacket(channelHandlerContext, packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!cause.getMessage().equals("Connection reset")) cause.printStackTrace();
    }
}
