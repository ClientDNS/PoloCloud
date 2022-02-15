package de.bytemc.network.codec.impl;

import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
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
