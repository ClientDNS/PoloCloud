package de.polocloud.api.network.packet.service;

import de.polocloud.api.network.packet.PacketHelper;
import de.polocloud.api.service.IService;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ServiceAddPacket implements IPacket {

    private IService service;

    @Override
    public void write(NetworkByteBuf byteBuf) {
        PacketHelper.writeService(byteBuf, this.service);
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.service = PacketHelper.readService(byteBuf);
    }

}
