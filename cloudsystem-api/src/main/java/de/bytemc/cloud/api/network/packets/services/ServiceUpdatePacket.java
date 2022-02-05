package de.bytemc.cloud.api.network.packets.services;

import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @Getter
public class ServiceUpdatePacket implements IPacket {

    private String service;
    private ServiceVisibility serviceVisibility;
    private ServiceState state;

    private int maxPlayers;
    private String motd;

    public ServiceUpdatePacket(IService service) {
        this.service = service.getName();
        this.serviceVisibility = service.getServiceVisibility();
        this.state = service.getServiceState();
        this.maxPlayers = service.getMaxPlayers();
        this.motd = service.getMotd();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(byteBuf, service);
        byteBuf.writeInt(serviceVisibility.ordinal());
        byteBuf.writeInt(state.ordinal());
        byteBuf.writeInt(maxPlayers);
        writeString(byteBuf, motd);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.service = readString(byteBuf);
        this.serviceVisibility = ServiceVisibility.values()[byteBuf.readInt()];
        this.state = ServiceState.values()[byteBuf.readInt()];
        this.maxPlayers = byteBuf.readInt();
        this.motd = readString(byteBuf);
    }
}
