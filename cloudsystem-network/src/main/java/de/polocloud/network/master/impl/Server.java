package de.polocloud.network.master.impl;

import com.google.common.collect.Lists;
import de.polocloud.network.codec.AbstractChannelInboundHandler;
import de.polocloud.network.codec.impl.SimplePacketAbstractHandler;
import de.polocloud.network.master.IServer;
import de.polocloud.network.master.cache.IAuthentication;
import de.polocloud.network.master.cache.IConnectedClient;
import de.polocloud.network.master.cache.SimpleConnectedClient;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.auth.HandshakeAuthenticationPacket;
import de.polocloud.network.pipeline.Pipeline;
import de.polocloud.network.promise.CommunicationPromise;
import de.polocloud.network.promise.ICommunicationPromise;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Server extends AbstractChannelInboundHandler implements IServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private EventExecutorGroup eventExecutorGroup;

    private ChannelFuture channelFuture;

    @Getter
    private final List<IConnectedClient> allCachedConnectedClients = new ArrayList<>();

    @Override
    public ICommunicationPromise<Void> connectEstablish(final String hostname, final int port) {
        var connectPromise = new CommunicationPromise<Void>();

        this.bossGroup = Pipeline.newEventLoopGroup();
        this.workerGroup = Pipeline.newEventLoopGroup();

        this.eventExecutorGroup = new DefaultEventExecutorGroup(20); // UNUSED?

        try {
            this.channelFuture = new ServerBootstrap()
                .group(this.bossGroup, this.workerGroup)
                .channel(Pipeline.getServerChannel())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        Pipeline.prepare(socketChannel.pipeline(), getSimpleChannelInboundHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .bind(hostname, port)
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
                .addListener(future -> {
                    if (future.isSuccess()) connectPromise.setSuccess(null);
                    else connectPromise.setFailure(future.cause());
                })
                .sync()
                .channel()
                .closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return connectPromise;
    }

    @Override
    public ICommunicationPromise<Void> shutdownConnection() {
        ICommunicationPromise<Void> shutdownBossPromise = new CommunicationPromise<>();
        ICommunicationPromise<Void> shutdownWorkerPromise = new CommunicationPromise<>();
        ICommunicationPromise<Void> executePromise = new CommunicationPromise<>();

        ICommunicationPromise<Void> promise = ICommunicationPromise.combineAll(Lists.newArrayList(shutdownBossPromise, shutdownBossPromise, executePromise));

        this.channelFuture.cancel(true);
        this.bossGroup.shutdownGracefully(0, 1, TimeUnit.MINUTES)
            .addListener(it -> shutdownBossPromise.setSuccess(null));
        this.workerGroup.shutdownGracefully(0, 1, TimeUnit.MINUTES)
            .addListener(it -> shutdownWorkerPromise.setSuccess(null));
        this.eventExecutorGroup.shutdownGracefully(0, 1, TimeUnit.MINUTES)
            .addListener(it -> executePromise.setSuccess(null));

        return promise;
    }

    public IConnectedClient getConnectedClientByChannel(final Channel channel) {
        return this.allCachedConnectedClients.stream().filter(it -> it.getChannel() == channel).findAny().orElse(null);
    }


    @Override
    public SimpleChannelInboundHandler<IPacket> getSimpleChannelInboundHandler() {
        return new SimplePacketAbstractHandler() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                addConnectedClient(ctx.channel());
            }

            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, IPacket packet) {
                var client = getConnectedClientByChannel(channelHandlerContext.channel());

                if (client == null) {
                    channelHandlerContext.close();
                    return;
                }

                if (!client.isAuthenticated()) {
                    authorize(client, packet, channelHandlerContext);
                    return;
                }
                super.channelRead0(channelHandlerContext, packet);
            }

            @Override
            public void channelInactive(final ChannelHandlerContext ctx) {
                closeClient(ctx);
            }

            @Override
            public void channelUnregistered(final ChannelHandlerContext ctx) {
                closeClient(ctx);
            }
        };
    }

    public void authorize(final IConnectedClient client, final IPacket packet, final ChannelHandlerContext context) {
        if (packet instanceof HandshakeAuthenticationPacket authPacket) {
            client.setName(authPacket.getClientName());
            client.setAuthenticated(true);
            onClientConnected(client);
        } else {
            context.close();
        }
    }

    public void closeClient(final ChannelHandlerContext context) {
        var connectedClient = getConnectedClientByChannel(context.channel());
        if (connectedClient == null) return;
        onClientDisconnected(connectedClient);
        getAllCachedConnectedClients().remove(connectedClient);
    }

    @Override
    public void addConnectedClient(final Channel channel) {
        this.getAllCachedConnectedClients().add(new SimpleConnectedClient(channel));
    }

    @Override
    public void sendPacketToAll(final IPacket packet) {
        getAllCachedConnectedClients().stream().filter(IAuthentication::isAuthenticated).forEach(it -> it.sendPacket(packet));
    }

    @Override
    public void sendPacketToClient(final IConnectedClient client, final IPacket iPacket) {
        client.sendPacket(iPacket);
    }

    public abstract void onClientDisconnected(final IConnectedClient client);

    public abstract void onClientConnected(final IConnectedClient client);

}
