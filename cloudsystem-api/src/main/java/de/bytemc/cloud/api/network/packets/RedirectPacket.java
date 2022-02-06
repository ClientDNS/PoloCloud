package de.bytemc.cloud.api.network.packets;

import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RedirectPacket implements IPacket {

    private String client;
    private IPacket packet;

    @Override
    public void write(ByteBuf byteBuf) {
        this.writeString(byteBuf, this.client);
        byteBuf.writeInt(NetworkManager.getPacketId(this.packet.getClass()));
        this.packet.write(byteBuf);
    }


    @SneakyThrows
    @Override
    public void read(ByteBuf byteBuf) {
        this.client = this.readString(byteBuf);
        var packetID = byteBuf.readInt();

        final Class<? extends IPacket> r = NetworkManager.getPacketClass(packetID);
        this.packet = r.getConstructor().newInstance();
        this.packet.read(byteBuf);
    }
}
