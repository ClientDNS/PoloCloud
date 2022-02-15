package de.bytemc.network.packets.defaultpackets;

import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HandshakeAuthenticationPacket implements IPacket {

    private String clientName;

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.clientName = byteBuf.readString();
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeString(this.clientName);
    }

}
