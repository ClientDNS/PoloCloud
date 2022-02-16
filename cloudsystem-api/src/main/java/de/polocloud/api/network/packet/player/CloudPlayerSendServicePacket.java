package de.polocloud.api.network.packet.player;

import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudPlayerSendServicePacket implements IPacket {

    private UUID uuid;
    private String service;

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.uuid = byteBuf.readUUID();
        this.service = byteBuf.readString();
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeUUID(uuid);
        byteBuf.writeString(service);
    }
}
