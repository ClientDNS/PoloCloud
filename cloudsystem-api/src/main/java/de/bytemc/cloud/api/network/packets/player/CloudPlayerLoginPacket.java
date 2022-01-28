package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @Getter @NoArgsConstructor
public class CloudPlayerLoginPacket implements IPacket {

    private String username;
    private UUID uuid;

    @Override
    public void read(ByteBuf byteBuf) {
        username = readString(byteBuf);
        uuid = readUUID(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(byteBuf, username);
        writeUUID(byteBuf, uuid);
    }
}
