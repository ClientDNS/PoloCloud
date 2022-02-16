package de.polocloud.api.network.packet.service;

import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ServiceRequestShutdownPacket implements IPacket {

    private String service;

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.service = byteBuf.readString();
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeString(this.service);
    }

}
