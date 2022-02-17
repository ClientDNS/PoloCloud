package de.polocloud.network.server;

import de.polocloud.network.codec.PacketDecoder;
import de.polocloud.network.codec.PacketEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public final class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyServer nettyServer;

    public NettyServerInitializer(final NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline()
            .addLast("packet-decoder", new PacketDecoder(this.nettyServer.getPacketHandler()))
            .addLast("packet-encoder", new PacketEncoder(this.nettyServer.getPacketHandler()))
            .addLast("handler", new NettyServerHandler(this.nettyServer));
    }

}
