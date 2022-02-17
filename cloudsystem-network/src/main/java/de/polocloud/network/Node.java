package de.polocloud.network;

import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.PacketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jetbrains.annotations.NotNull;

public abstract class Node {

    protected final PacketHandler packetHandler;
    protected final String name;
    protected final NetworkType networkType;

    protected Node(final PacketHandler packetHandler, final String name, final NetworkType networkType) {
        this.packetHandler = packetHandler;
        this.name = name;
        this.networkType = networkType;
    }

    public abstract void connect(@NotNull String host, int port);

    public abstract void close();

    public PacketHandler getPacketHandler() {
        return this.packetHandler;
    }

    public String getName() {
        return this.name;
    }

    public NetworkType getNetworkType() {
        return this.networkType;
    }

    protected EventLoopGroup newEventLoopGroup() {
        return this.newEventLoopGroup(0);
    }

    protected EventLoopGroup newEventLoopGroup(final int threads) {
        return (Epoll.isAvailable() ? new EpollEventLoopGroup(threads) : new NioEventLoopGroup(threads));
    }

    protected Class<? extends ServerSocketChannel> getServerSocketChannel() {
        return (Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
    }

    protected Class<? extends SocketChannel> getSocketChannel() {
        return (Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class);
    }

    public void onActivated(final ChannelHandlerContext channelHandlerContext) {}

    public void onClose(final ChannelHandlerContext channelHandlerContext) {}

    public void sendPacket(final @NotNull Packet packet) {

    }

}
