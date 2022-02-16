package de.polocloud.api.network.packet.player;

import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
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
    public void read(NetworkByteBuf byteBuf) {
        this.username = byteBuf.readString();
        this.uuid = byteBuf.readUUID();
        this.proxyServer = byteBuf.readString();
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeString(this.username);
        byteBuf.writeUUID(this.uuid);
        byteBuf.writeString(this.proxyServer);
    }

}
