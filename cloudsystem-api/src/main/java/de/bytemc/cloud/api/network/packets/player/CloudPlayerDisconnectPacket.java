package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @Getter
public class CloudPlayerDisconnectPacket implements IPacket {

    private UUID uuid;

    @Override
    public void write(ByteBuf byteBuf) {
        writeUUID(byteBuf , uuid);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.uuid = readUUID(byteBuf);
    }
}
