package de.polocloud.network.client;

import de.polocloud.network.NetworkType;
import de.polocloud.network.Node;
import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.PacketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import org.jetbrains.annotations.NotNull;

public class NettyClient extends Node {

    private EventLoopGroup eventLoopGroup;

    private Channel channel;

    public NettyClient(final PacketHandler packetHandler, final String name, final NetworkType networkType) {
        super(packetHandler, name, networkType);
    }

    @Override
    public void connect(@NotNull String host, int port) {
        this.eventLoopGroup = this.newEventLoopGroup();

        this.channel = new Bootstrap()
            .channel(this.getSocketChannel())
            .group(this.eventLoopGroup)
            .handler(new NettyClientInitializer(this))
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.AUTO_READ, true)
            .connect(host, port)
            .syncUninterruptibly()
            .channel();
    }

    @Override
    public void close() {
        this.channel.close();

        this.eventLoopGroup.shutdownGracefully();
    }

    public void sendPacket(final @NotNull Packet packet) {
        this.channel.writeAndFlush(packet);
    }

    public void onActivated(final ChannelHandlerContext channelHandlerContext) {}

    public void onClose(final ChannelHandlerContext channelHandlerContext) {}

}
