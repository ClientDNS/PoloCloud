package de.polocloud.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketLengthDeserializer extends ByteToMessageDecoder {

    private final int byteSize = 5;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        try {
            if (!channelHandlerContext.channel().isActive()) {
                byteBuf.skipBytes(byteBuf.readableBytes());
                return;
            }
            if (!byteBuf.isReadable()) return;

            final var readerIndex = byteBuf.readerIndex();
            final var bytes = new byte[this.byteSize];

            for (var i = 0; i < this.byteSize; i++) {
                if (!byteBuf.isReadable()) {
                    byteBuf.readerIndex(readerIndex);
                    return;
                }

                bytes[i] = byteBuf.readByte();
                if (bytes[i] >= 0) {
                    final var buf = Unpooled.wrappedBuffer(bytes);

                    try {
                        final var length = this.readVarIntUnchecked(buf);

                        if (byteBuf.readableBytes() < length) {
                            byteBuf.readerIndex(readerIndex);
                            return;
                        }

                        list.add(byteBuf.readBytes(length));
                    } finally {
                        buf.release();
                    }
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int readVarIntUnchecked(ByteBuf byteBuf) {
        var i = 0;
        final var maxRead = Math.min(this.byteSize, byteBuf.readableBytes());
        for (var j = 0; j < maxRead; j++) {
            var k = byteBuf.readByte();
            i |= (k & 127) << j * 7;
            if ((k & 128) != 128) {
                return i;
            }
        }
        return i;
    }

}
