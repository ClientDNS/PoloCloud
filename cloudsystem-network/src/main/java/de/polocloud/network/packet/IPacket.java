package de.polocloud.network.packet;

public interface IPacket {

    void read(NetworkByteBuf byteBuf);

    void write(NetworkByteBuf byteBuf);

}
