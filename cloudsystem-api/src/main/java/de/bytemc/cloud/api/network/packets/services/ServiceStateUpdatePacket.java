package de.bytemc.cloud.api.network.packets.services;

import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Getter
public class ServiceStateUpdatePacket implements IPacket {

    private String service;
    private ServiceState serviceState;

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(byteBuf, service);
        byteBuf.writeInt(serviceState.ordinal());
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.service = readString(byteBuf);
        this.serviceState = ServiceState.values()[byteBuf.readInt()];
    }
}
