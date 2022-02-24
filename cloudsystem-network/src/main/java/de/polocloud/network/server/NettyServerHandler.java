package de.polocloud.network.server;

import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.auth.NodeHandshakeAuthenticationPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public final class NettyServerHandler extends SimpleChannelInboundHandler<Packet> {

    private final NettyServer nettyServer;

    public NettyServerHandler(final NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if (packet instanceof NodeHandshakeAuthenticationPacket authenticationPacket) {
            this.nettyServer.addClient(
                channelHandlerContext.channel(),
                authenticationPacket.getName(),
                authenticationPacket.getType());
        } else {
            this.nettyServer.getPacketHandler().call(channelHandlerContext, packet);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!cause.getMessage().equals("Connection reset")) cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.nettyServer.closeClient(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        this.nettyServer.closeClient(ctx);
    }

}
