package de.polocloud.api.network.packet.service;

import de.polocloud.api.service.CloudService;
import de.polocloud.network.packet.NetworkBuf;
import de.polocloud.network.packet.Packet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@Getter
public class ServiceUpdatePacket implements Packet {

    private String service;
    private String state;

    private int maxPlayers;
    private String motd;

    public ServiceUpdatePacket(CloudService service) {
        this.service = service.getName();
        this.state = service.getState();
        this.maxPlayers = service.getMaxPlayers();
        this.motd = service.getMotd();
    }

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        byteBuf
            .writeString(this.service)
            .writeString(this.state)
            .writeInt(this.maxPlayers)
            .writeString(this.motd);
    }

    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        this.service = byteBuf.readString();
        this.state = byteBuf.readString();
        this.maxPlayers = byteBuf.readInt();
        this.motd = byteBuf.readString();
    }

}
