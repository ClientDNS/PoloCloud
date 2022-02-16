package de.polocloud.network.codec;

import de.polocloud.network.NetworkManager;
import de.polocloud.network.codec.exception.PacketReadException;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.SneakyThrows;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int index = byteBuf.readInt();
        NetworkManager.getPacketClass(index).ifPresentOrElse(clazz -> list.add(initializePacket(clazz, byteBuf, index)), () -> {
            throw new NullPointerException("Couldn't find id of packet " + index);
        });
    }

    @SneakyThrows
    private IPacket initializePacket(final Class<? extends IPacket> clazz, ByteBuf byteBuf, int index) {
        IPacket packet = clazz.getDeclaredConstructor().newInstance();
        try {
            packet.read(new NetworkByteBuf(byteBuf));
        }catch (Exception exception) {
            throw new PacketReadException(clazz, index);
        }
        return packet;
    }

}

