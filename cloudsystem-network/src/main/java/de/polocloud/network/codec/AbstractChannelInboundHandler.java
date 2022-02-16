package de.polocloud.network.codec;

import de.polocloud.network.packet.IPacket;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractChannelInboundHandler {

    public abstract SimpleChannelInboundHandler<IPacket> getSimpleChannelInboundHandler();

}
