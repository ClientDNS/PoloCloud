package de.polocloud.network.server;

import de.polocloud.network.NetworkType;
import de.polocloud.network.Node;
import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.PacketHandler;
import de.polocloud.network.server.client.ConnectedClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NettyServer extends Node {

    private final Map<Channel, ConnectedClient> connectedClients;

    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;

    private ChannelFuture channelFuture;

    public NettyServer(final PacketHandler packetHandler, final String name, final NetworkType networkType) {
        super(packetHandler, name, networkType);
        this.connectedClients = new ConcurrentHashMap<>();
    }

    public void connect(final @NotNull String host, final int port) {
        this.bossEventLoopGroup = this.newEventLoopGroup(1);
        this.workerEventLoopGroup = this.newEventLoopGroup();

        try {
            this.channelFuture = new ServerBootstrap()
                .channel(this.getServerSocketChannel())
                .group(this.bossEventLoopGroup, this.workerEventLoopGroup)
                .childHandler(new NettyServerInitializer(this))
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.AUTO_READ, true)
                .bind(host, port)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                .sync()
                .channel()
                .closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        this.channelFuture.cancel(true);

        this.bossEventLoopGroup.shutdownGracefully();
        this.workerEventLoopGroup.shutdownGracefully();
    }

    public void addClient(final Channel channel, final String name, final NetworkType networkType) {
        final var client = new ConnectedClient(name, channel, networkType);
        this.connectedClients.put(channel, client);
        switch (networkType) {
            case NODE -> this.onNodeConnected(client);
            case WRAPPER -> this.onServiceConnected(client);
        }
    }

    public void closeClient(final ChannelHandlerContext channelHandlerContext) {
        final var client = this.connectedClients.remove(channelHandlerContext.channel());
        if (client == null) return;
        switch (client.networkType()) {
            case NODE -> this.onNodeDisconnected(client);
            case WRAPPER -> this.onServiceDisconnected(client);
        }
        channelHandlerContext.close();
    }

    public ConnectedClient getClient(final Channel channel) {
        return this.connectedClients.get(channel);
    }

    public Optional<ConnectedClient> getClient(final @NotNull String name) {
        return this.getClients().stream().filter(connectedClient -> connectedClient.name().equalsIgnoreCase(name)).findFirst();
    }

    public Collection<ConnectedClient> getClients() {
        return this.connectedClients.values();
    }

    public List<ConnectedClient> getServices() {
        return this.getClients().stream().filter(connectedClient -> connectedClient.networkType() == NetworkType.WRAPPER).toList();
    }

    public void sendPacketToAll(final Packet packet) {
        this.connectedClients.keySet().forEach(channel -> channel.writeAndFlush(packet));
    }

    public void sendPacketToType(final Packet packet, final NetworkType networkType) {
        this.connectedClients.values().stream().filter(connectedClient -> connectedClient.networkType() == networkType)
            .forEach(connectedClient -> connectedClient.channel().writeAndFlush(packet));
    }

    public abstract void onNodeConnected(final ConnectedClient connectedClient);

    public abstract void onNodeDisconnected(final ConnectedClient connectedClient);

    public abstract void onServiceConnected(final ConnectedClient connectedClient);

    public abstract void onServiceDisconnected(final ConnectedClient connectedClient);

}
