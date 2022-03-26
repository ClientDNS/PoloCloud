package de.polocloud.network.packet.auth;

import de.polocloud.network.NetworkType;
import de.polocloud.network.packet.NetworkBuf;
import de.polocloud.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

public final class NodeHandshakeAuthenticationPacket implements Packet {

    private String name;
    private NetworkType type;

    public NodeHandshakeAuthenticationPacket() {
    }

    public NodeHandshakeAuthenticationPacket(final String name, final NetworkType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        this.type = byteBuf.readEnum();
        this.name = byteBuf.readString();
    }

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        byteBuf
            .writeEnum(this.type)
            .writeString(this.name);
    }

    public String getName() {
        return this.name;
    }

    public NetworkType getType() {
        return this.type;
    }

}
