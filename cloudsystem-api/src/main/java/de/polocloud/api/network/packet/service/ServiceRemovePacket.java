package de.polocloud.api.network.packet.service;

import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.NetworkBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServiceRemovePacket implements Packet {

    private String service;

    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        this.service = byteBuf.readString();
    }

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        byteBuf.writeString(this.service);
    }

}
