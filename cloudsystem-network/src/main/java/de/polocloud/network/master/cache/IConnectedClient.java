package de.polocloud.network.master.cache;

import de.polocloud.network.packet.IPacket;
import de.polocloud.network.promise.CommunicationPromise;
import de.polocloud.network.promise.ICommunicationPromise;
import io.netty.channel.Channel;

import java.util.Objects;

public interface IConnectedClient extends IAuthentication {

    Channel getChannel();

    String getName();

    void setName(String name);

    default ICommunicationPromise<Void> close() {
        ICommunicationPromise<Void> closeFuture = new CommunicationPromise<>();
        getChannel().close().addListener(future -> {
            if (future.isSuccess()) closeFuture.setSuccess(null);
            else closeFuture.setFailure(future.cause());
        });
        return closeFuture;
    }

    default void sendPacket(IPacket packet) {
        Objects.requireNonNull(getChannel(), "Channel is null: " + getName());
        getChannel().writeAndFlush(packet);
    }

}
