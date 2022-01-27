package de.bytemc.cloud.api.network.packets;

import de.bytemc.network.NetworkManager;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor @NoArgsConstructor @Getter
public class RedirectPacket implements IPacket {

    private String node;
    private IPacket packet;

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(byteBuf, node);
        byteBuf.writeInt(NetworkManager.getIndexFromPacket(packet.getClass()));
        packet.write(byteBuf);
    }


    @SneakyThrows
    @Override
    public void read(ByteBuf byteBuf) {
        this.node = readString(byteBuf);
        var packetID = byteBuf.readInt();

        Class<? extends IPacket> r = NetworkManager.getClassFromId(packetID);
        packet = r.getConstructor().newInstance();
        packet.read(byteBuf);
    }
}
