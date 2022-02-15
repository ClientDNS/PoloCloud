package de.bytemc.network.cluster.impl.client;

import de.bytemc.network.client.impl.Client;
import de.bytemc.network.cluster.types.NetworkType;
import de.bytemc.network.codec.impl.SimplePacketAbstractHandler;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.defaultpackets.NodeHandshakeAuthenticationPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class NodeClient extends Client {

    private final NetworkType type;

    public NodeClient(String clientName, NetworkType type) {
        super(clientName);
        this.type = type;
    }

    @Override
    public SimpleChannelInboundHandler<IPacket> getSimpleChannelInboundHandler() {
        return new SimplePacketAbstractHandler() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                NodeClient.this.onActivated(ctx);
                NodeClient.this.sendPacket(new NodeHandshakeAuthenticationPacket(getClientName(), NodeClient.this.type));
            }

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) {
                onClose(ctx);
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                onClose(ctx);
            }
        };
    }

    public abstract void onActivated(ChannelHandlerContext channelHandlerContext);

    public abstract void onClose(final ChannelHandlerContext channelHandlerContext);

}
