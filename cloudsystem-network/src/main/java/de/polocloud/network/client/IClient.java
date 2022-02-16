package de.polocloud.network.client;

import de.polocloud.network.packet.IPacket;
import de.polocloud.network.promise.ICommunicationPromise;
import io.netty.channel.Channel;

public interface IClient {

    ICommunicationPromise<Channel> connectEstablishment(final String hostname, final int port);

    ICommunicationPromise<Void> shutdown();

    void sendPacket(IPacket packet);

}
