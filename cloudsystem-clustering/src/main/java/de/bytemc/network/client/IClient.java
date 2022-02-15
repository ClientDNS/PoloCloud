package de.bytemc.network.client;

import de.bytemc.network.packets.IPacket;
import de.bytemc.network.promise.ICommunicationPromise;
import io.netty.channel.Channel;

public interface IClient {

    ICommunicationPromise<Channel> connectEstablishment(final String hostname, final int port);

    ICommunicationPromise<Void> shutdown();

    void sendPacket(IPacket packet);

}
