package de.polocloud.api.network.packet.service;

import de.polocloud.api.service.IService;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.api.service.utils.ServiceVisibility;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
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
    public void write(NetworkByteBuf byteBuf) {
        byteBuf. writeString(service);
        byteBuf.writeInt(serviceVisibility.ordinal());
        byteBuf.writeInt(state.ordinal());
        byteBuf.writeInt(maxPlayers);
        byteBuf.writeString(motd);
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.service =   byteBuf.readString();
        this.serviceVisibility = ServiceVisibility.values()[byteBuf.readInt()];
        this.state = ServiceState.values()[byteBuf.readInt()];
        this.maxPlayers = byteBuf.readInt();
        this.motd =   byteBuf.readString();
    }
}
