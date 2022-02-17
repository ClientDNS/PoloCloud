package de.polocloud.network.server.client;

import de.polocloud.network.NetworkType;
import de.polocloud.network.packet.Packet;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;

public record ConnectedClient(String name, Channel channel, NetworkType networkType) {

    public void sendPacket(@NotNull Packet packet) {
        this.channel.writeAndFlush(packet);
    }

}
