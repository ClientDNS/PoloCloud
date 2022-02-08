package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
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

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.username = byteBuf.readString();
        this.uuid = byteBuf.readUUID();
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeString(this.username);
        byteBuf.writeUUID(this.uuid);
    }

}
