package de.bytemc.cloud.api.network.packets.services;

import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServiceShutdownPacket implements IPacket {

    private String service;

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeString(this.service);
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.service =   byteBuf.readString();
    }

}
