package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CloudPlayerUpdatePacket implements IPacket {

    private UUID uuid;
    private IService proxyServer;
    private IService server;

    public CloudPlayerUpdatePacket(ICloudPlayer cloudPlayer) {
        this.uuid = cloudPlayer.getUniqueId();
        this.proxyServer = cloudPlayer.getProxyServer();
        this.server = cloudPlayer.getServer();
    }

    @Override
    public void write(ByteBuf byteBuf) {
       writeUUID(byteBuf, uuid);
       writeString(byteBuf, proxyServer.getName());
       writeString(byteBuf, server.getName());
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.uuid = readUUID(byteBuf);
        this.proxyServer = CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(readString(byteBuf));
        this.server = CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(readString(byteBuf));
    }
}
