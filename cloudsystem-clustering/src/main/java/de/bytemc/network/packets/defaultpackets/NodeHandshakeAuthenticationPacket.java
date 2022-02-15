package de.bytemc.network.packets.defaultpackets;

import de.bytemc.network.cluster.types.NetworkType;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
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
