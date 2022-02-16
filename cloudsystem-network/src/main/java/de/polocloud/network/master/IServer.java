package de.polocloud.network.master;

import de.polocloud.network.master.cache.IConnectedClient;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.promise.ICommunicationPromise;
import io.netty.channel.Channel;

import java.util.List;

public interface IServer {

    List<IConnectedClient> getAllCachedConnectedClients();

    void addConnectedClient(Channel client);

    ICommunicationPromise<Void> connectEstablish(final String hostname, final int port);

    ICommunicationPromise<Void> shutdownConnection();

    void sendPacketToAll(IPacket packet);

    void sendPacketToClient(IConnectedClient client, IPacket packet);

}
