package de.polocloud.network.packet;

import io.netty.channel.ChannelHandlerContext;

public interface IPacketListener<T> {

    void handle(ChannelHandlerContext channelHandlerContext, T t);

}
