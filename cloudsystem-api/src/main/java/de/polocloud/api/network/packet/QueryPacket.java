package de.polocloud.api.network.packet;

import de.polocloud.api.CloudAPI;
import de.polocloud.network.packet.NetworkBuf;
import de.polocloud.network.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QueryPacket implements Packet {

    private Packet packet;
    private QueryState state;

    public enum QueryState {
        FIRST_RESPONSE,
        SECOND_RESPONSE
    }

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        // write packet id to init
        byteBuf.writeInt(CloudAPI.getInstance().getPacketHandler().getPacketId(this.packet.getClass()));
        this.packet.write(byteBuf);

        // write state for indexing
        byteBuf.writeInt(this.state.ordinal());
    }

    @SneakyThrows
    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        final var packetId = byteBuf.readInt();

        this.initPacket(byteBuf, CloudAPI.getInstance().getPacketHandler().getPacketClass(packetId));
    }

    @SneakyThrows
    private void initPacket(NetworkBuf byteBuf, Class<? extends Packet> it){
        this.packet = it.getConstructor().newInstance();
        this.packet.read(byteBuf);

        this.state = QueryState.values()[byteBuf.readInt()];
    }

}
