package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudPlayerMessagePacket implements IPacket {

    private UUID uuid;
    private String message;

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeUUID(uuid);
        byteBuf.writeString(message);
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.uuid = byteBuf.readUUID();
        this.message = byteBuf.readString();
    }
}
