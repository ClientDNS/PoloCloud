package de.polocloud.network.packet.auth;

import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
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
