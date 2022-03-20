package de.polocloud.api.network.packet.service;

import de.polocloud.network.packet.NetworkBuf;
import de.polocloud.network.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCopyRequestPacket implements Packet {

    private String service;

    @Override
    public void write(@NotNull NetworkBuf networkBuf) {
        networkBuf.writeString(service);
    }

    @Override
    public void read(@NotNull NetworkBuf networkBuf) {
        this.service = networkBuf.readString();
    }
}
