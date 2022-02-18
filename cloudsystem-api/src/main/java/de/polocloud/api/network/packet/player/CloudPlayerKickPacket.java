package de.polocloud.api.network.packet.player;

import de.polocloud.network.packet.NetworkBuf;
import de.polocloud.network.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudPlayerKickPacket implements Packet {

    private UUID uuid;
    private String proxyService;
    private String reason;

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        byteBuf.writeUUID(uuid);
        byteBuf.writeString(proxyService);
        byteBuf.writeString(reason);
    }

    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        this.uuid =  byteBuf.readUUID();
        this.proxyService =  byteBuf.readString();
        this.reason =  byteBuf.readString();
    }

}
