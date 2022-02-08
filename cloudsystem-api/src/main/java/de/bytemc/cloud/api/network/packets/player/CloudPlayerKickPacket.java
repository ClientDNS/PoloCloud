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
public class CloudPlayerKickPacket implements IPacket {

    private UUID uuid;
    private String proxyService;
    private String reason;

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeUUID(uuid);
        byteBuf.writeString(proxyService);
        byteBuf.writeString(reason);
    }


    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.uuid =  byteBuf.readUUID();
        this.proxyService =  byteBuf.readString();
        this.reason =  byteBuf.readString();
    }
}
