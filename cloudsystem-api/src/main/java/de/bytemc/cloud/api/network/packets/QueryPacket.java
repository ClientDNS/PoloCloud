package de.bytemc.cloud.api.network.packets;

import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QueryPacket implements IPacket {

    private IPacket packet;
    private QueryState state;

    public enum QueryState {
        FIRST_RESPONSE,
        SECOND_RESPONSE;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        //write packet id to init
        byteBuf.writeInt(NetworkManager.getPacketId(this.packet.getClass()));
        packet.write(byteBuf);

        //write state for indexing
        byteBuf.writeInt(state.ordinal());
    }

    @SneakyThrows
    @Override
    public void read(ByteBuf byteBuf) {
        var packetID = byteBuf.readInt();

        final Class<? extends IPacket> r = NetworkManager.getPacketClass(packetID);
        this.packet = r.getConstructor().newInstance();
        this.packet.read(byteBuf);

        state = QueryState.values()[byteBuf.readInt()];
    }
}
