package de.polocloud.api.network.packet;

import de.polocloud.network.NetworkManager;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
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
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeString(this.client);
        NetworkManager.getPacketId(this.packet.getClass()).ifPresent(it -> {
            byteBuf.writeInt(it);
            this.packet.write(byteBuf);
        });
    }


    @SneakyThrows
    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.client = byteBuf.readString();
        var packetID = byteBuf.readInt();

        NetworkManager.getPacketClass(packetID).ifPresent(it -> initPacket(byteBuf, it));
    }

    @SneakyThrows
    public void initPacket(NetworkByteBuf byteBuf, Class<? extends IPacket> it) {
        this.packet = it.getConstructor().newInstance();
        this.packet.read(byteBuf);
    }

}
