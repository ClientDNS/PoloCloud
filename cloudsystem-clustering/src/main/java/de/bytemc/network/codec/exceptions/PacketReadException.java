package de.bytemc.network.codec.exceptions;

import de.bytemc.network.packets.IPacket;
import lombok.Getter;

@Getter
public class PacketReadException extends Exception {

    private final Class<? extends IPacket> packet;
    private final int id;

    public PacketReadException(Class<? extends IPacket> packet, final int id) {
        super("An error occurred while reading packet with id " + id + ": " + packet.getName());

        this.packet = packet;
        this.id = id;
    }

}
