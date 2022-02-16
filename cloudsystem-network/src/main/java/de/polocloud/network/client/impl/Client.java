package de.polocloud.network.client.impl;

import de.polocloud.network.client.IClient;
import de.polocloud.network.codec.AbstractChannelInboundHandler;
import de.polocloud.network.codec.impl.SimplePacketAbstractHandler;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.auth.HandshakeAuthenticationPacket;
import de.polocloud.network.pipeline.Pipeline;
import de.polocloud.network.promise.CommunicationPromise;
import de.polocloud.network.promise.ICommunicationPromise;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.channels.ConnectionPendingException;

@RequiredArgsConstructor
public abstract class Client extends AbstractChannelInboundHandler implements IClient {

    private EventLoopGroup workerGroup;
    private boolean active = false;
    private Channel channel;

    @Getter
    private final String clientName;

    @Override
    public ICommunicationPromise<Channel> connectEstablishment(String hostname, int port) {
        var establishPromise = new CommunicationPromise<Channel>();

        if (active) {
            establishPromise.setFailure(new ConnectionPendingException());
            return establishPromise;
        }

        this.active = true;
        this.workerGroup = Pipeline.newEventLoopGroup();

        final var channelPromise = new CommunicationPromise<>();
        this.channel = new Bootstrap()
            .channel(Pipeline.getChannel())
            .group(this.workerGroup)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    Pipeline.prepare(socketChannel.pipeline(), getSimpleChannelInboundHandler());
                }
            })
            .option(ChannelOption.SO_KEEPALIVE, true)
            .connect(hostname, port)
            .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
            .addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
            .addListener(future -> {
                if (future.isSuccess()) channelPromise.setSuccess(null);
                else {
                    channelPromise.setFailure(future.cause());
                    workerGroup.shutdownGracefully();
                }
            }).channel();
        channelPromise.addResultListener(unused -> establishPromise.setSuccess(this.channel)).addFailureListener(establishPromise::setFailure);
        return establishPromise;
    }

    @Override
    public ICommunicationPromise<Void> shutdown() {
        var shutdownPromise = new CommunicationPromise<Void>();
        this.workerGroup.shutdownGracefully().addListener(future -> {
            if (future.isSuccess()) shutdownPromise.setSuccess(null);
            else shutdownPromise.setFailure(future.cause());
        });
        return shutdownPromise;
    }

    @Override
    public void sendPacket(final IPacket packet) {
        this.channel.writeAndFlush(packet);
    }

    @Override
    public SimpleChannelInboundHandler<IPacket> getSimpleChannelInboundHandler() {
        return new SimplePacketAbstractHandler() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                sendPacket(new HandshakeAuthenticationPacket(getClientName()));
            }
        };
    }

}
