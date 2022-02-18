package de.polocloud.network.codec;

import de.polocloud.network.packet.NetworkBuf;
import de.polocloud.network.packet.PacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class PacketDecoder extends ByteToMessageDecoder {

    private final PacketHandler packetHandler;

    public PacketDecoder(final PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        final var index = byteBuf.readInt();
        final var clazz = this.packetHandler.getPacketClass(index);
        if (clazz != null) {
            try {
                final var packet = clazz.getDeclaredConstructor().newInstance();
                packet.read(new NetworkBuf(byteBuf));
                list.add(packet);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

}
