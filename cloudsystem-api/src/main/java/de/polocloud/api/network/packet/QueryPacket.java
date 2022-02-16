package de.polocloud.api.network.packet;

import de.polocloud.network.NetworkManager;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
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
        SECOND_RESPONSE
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        //write packet id to init

        NetworkManager.getPacketId(this.packet.getClass()).ifPresentOrElse(it -> {
            byteBuf.writeInt(it);
            packet.write(byteBuf);

            //write state for indexing
            byteBuf.writeInt(state.ordinal());
        }, () -> {
            throw new NullPointerException("Cannot write QueryPacket: Write ByteBuf(Packet Id not found)");
        });
    }

    @SneakyThrows
    @Override
    public void read(NetworkByteBuf byteBuf) {
        var packetID = byteBuf.readInt();

        NetworkManager.getPacketClass(packetID).ifPresentOrElse(it -> initPacket(byteBuf, it), () -> {
            throw new NullPointerException("Cannot read QueryPacket: Read ByteBuf(Packet not found)");
        });
    }

    @SneakyThrows
    private void initPacket(NetworkByteBuf byteBuf, Class<? extends IPacket> it){
        this.packet = it.getConstructor().newInstance();
        this.packet.read(byteBuf);

        state = QueryState.values()[byteBuf.readInt()];
    }

}
