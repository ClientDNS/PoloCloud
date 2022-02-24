package de.polocloud.network.packet;

import io.netty.channel.ChannelHandlerContext;

public interface PacketListener<T> {

    void handle(final ChannelHandlerContext channelHandlerContext, final T t);

}
