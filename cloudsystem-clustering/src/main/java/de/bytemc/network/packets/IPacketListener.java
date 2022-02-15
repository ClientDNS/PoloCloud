package de.bytemc.network.packets;

import io.netty.channel.ChannelHandlerContext;

public interface IPacketListener<T> {

    void handle(ChannelHandlerContext channelHandlerContext, T t);

}
