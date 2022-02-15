package de.bytemc.network.codec;

import de.bytemc.network.packets.IPacket;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AbstractChannelInboundHandler {

    public abstract SimpleChannelInboundHandler<IPacket> getSimpleChannelInboundHandler();

}
