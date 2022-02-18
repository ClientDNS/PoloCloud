package de.polocloud.api.network.packet.service;

import de.polocloud.api.network.packet.PacketHelper;
import de.polocloud.api.service.IService;
import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.NetworkBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ServiceAddPacket implements Packet {

    private IService service;

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        PacketHelper.writeService(byteBuf, this.service);
    }

    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        this.service = PacketHelper.readService(byteBuf);
    }

}
