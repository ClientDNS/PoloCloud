package de.bytemc.network.codec;

import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<IPacket> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IPacket packet, ByteBuf output) {
        NetworkManager.getPacketId(packet.getClass()).ifPresentOrElse(id -> {
            output.writeInt(id);
            packet.write(new NetworkByteBuf(output));
        }, () -> {
            throw new NullPointerException("Couldn't find id of packet " + packet.getClass().getSimpleName());
        });
    }
}
