package de.polocloud.network.master.cache;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SimpleConnectedClient implements IConnectedClient {

    private String name = "unknown";
    private final Channel channel;
    private boolean authenticated = false;

}
