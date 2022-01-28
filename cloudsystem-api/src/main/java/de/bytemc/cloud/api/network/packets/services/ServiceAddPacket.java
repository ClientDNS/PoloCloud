package de.bytemc.cloud.api.network.packets.services;

import de.bytemc.cloud.api.network.packets.PacketHelper;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @Getter @NoArgsConstructor
public class ServiceAddPacket implements IPacket {

    private IService service;

    @Override
    public void write(ByteBuf byteBuf) {
        PacketHelper.writeService(service, byteBuf, this);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        service = PacketHelper.readService(byteBuf, this);
    }
}
