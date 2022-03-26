package de.polocloud.api.network.packet.player;

import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.NetworkBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudPlayerMessagePacket implements Packet {

    private UUID uuid;
    private String message;

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        byteBuf
            .writeUUID(this.uuid)
            .writeString(this.message);
    }

    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        this.uuid = byteBuf.readUUID();
        this.message = byteBuf.readString();
    }

}
