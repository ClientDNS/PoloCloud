package de.polocloud.api.network.packet;

import de.polocloud.api.CloudAPI;
import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.NetworkBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RedirectPacket implements Packet {

    private String client;
    private Packet packet;

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        byteBuf.writeString(this.client);
        byteBuf.writeInt(CloudAPI.getInstance().getPacketHandler().getPacketId(this.packet.getClass()));
        this.packet.write(byteBuf);
    }


    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        this.client = byteBuf.readString();
        this.initPacket(byteBuf, CloudAPI.getInstance().getPacketHandler().getPacketClass(byteBuf.readInt()));
    }

    public void initPacket(NetworkBuf byteBuf, Class<? extends Packet> it) {
        try {
            this.packet = it.getConstructor().newInstance();
            this.packet.read(byteBuf);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
