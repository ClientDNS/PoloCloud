package de.bytemc.network.pipeline;

import de.bytemc.network.codec.PacketDecoder;
import de.bytemc.network.codec.PacketEncoder;
import de.bytemc.network.codec.prepender.NettyPacketLengthDeserializer;
import de.bytemc.network.codec.prepender.NettyPacketLengthSerializer;
import de.bytemc.network.packets.IPacket;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;

public class Pipeline {

    public static EventLoopGroup newEventLoopGroup() {
        return Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    public static EventLoopGroup newEventLoopGroup(final int threads, final ThreadFactory factory) {
        return Epoll.isAvailable() ? new EpollEventLoopGroup(threads, factory) : new NioEventLoopGroup(threads, factory);
    }

    public static Class<? extends Channel> getChannel() {
        return Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends ServerChannel> getServerChannel() {
        return Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static void prepare(final ChannelPipeline pipeline, final SimpleChannelInboundHandler<IPacket> handling) {
        pipeline.addLast(new NettyPacketLengthDeserializer()).addLast(new PacketDecoder())
            .addLast(new NettyPacketLengthSerializer()).addLast(new PacketEncoder()).addLast(handling);
    }

}
