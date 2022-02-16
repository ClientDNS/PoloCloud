package de.polocloud.network.packet.auth;

import de.polocloud.network.cluster.type.NetworkType;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NodeHandshakeAuthenticationPacket implements IPacket {

    private String clientName;
    private NetworkType type;

    @Override
    public void read(NetworkByteBuf byteBuf) {
        type = byteBuf.readEnum();
        clientName = byteBuf.readString();
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeEnum(this.type);
        byteBuf.writeString(this.clientName);
    }

}
