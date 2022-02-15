package de.bytemc.network.packets;

public interface IPacket {

    void read(NetworkByteBuf byteBuf);

    void write(NetworkByteBuf byteBuf);

}
