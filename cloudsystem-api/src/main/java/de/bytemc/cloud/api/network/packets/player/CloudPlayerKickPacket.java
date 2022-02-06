package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudPlayerKickPacket implements IPacket {

    private UUID uuid;
    private String proxyService;
    private String reason;

    @Override
    public void write(ByteBuf byteBuf) {
        writeUUID(byteBuf, uuid);
        writeString(byteBuf, proxyService);
        writeString(byteBuf, reason);
    }


    @Override
    public void read(ByteBuf byteBuf) {
        this.uuid = readUUID(byteBuf);
        this.proxyService = readString(byteBuf);
        this.reason = readString(byteBuf);
    }
}
