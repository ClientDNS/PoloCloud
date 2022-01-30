package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CloudPlayerLoginPacket implements IPacket {

    private String username;
    private UUID uuid;
    private String proxyServer;

    @Override
    public void read(ByteBuf byteBuf) {
        this.username = this.readString(byteBuf);
        this.uuid = this.readUUID(byteBuf);
        this.proxyServer = this.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        this.writeString(byteBuf, this.username);
        this.writeUUID(byteBuf, this.uuid);
        this.writeString(byteBuf, this.proxyServer);
    }

}
