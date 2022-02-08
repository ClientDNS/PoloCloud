package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CloudPlayerDisconnectPacket implements IPacket {

    private UUID uuid;
    private String name;

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeUUID(this.uuid);
        byteBuf.writeString(this.name);
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.uuid = byteBuf.readUUID();
        this.name = byteBuf.readString();
    }

}
