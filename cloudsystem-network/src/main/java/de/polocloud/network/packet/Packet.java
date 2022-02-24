package de.polocloud.network.packet;

import org.jetbrains.annotations.NotNull;

public interface Packet {

    void write(@NotNull NetworkBuf networkBuf);

    void read(@NotNull NetworkBuf networkBuf);

}
