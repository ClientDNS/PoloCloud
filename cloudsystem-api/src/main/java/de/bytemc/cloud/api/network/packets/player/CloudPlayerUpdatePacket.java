package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CloudPlayerUpdatePacket implements IPacket {

    private UUID uuid;
    private IService server;

    public CloudPlayerUpdatePacket(ICloudPlayer cloudPlayer) {
        this.uuid = cloudPlayer.getUniqueId();
        this.server = cloudPlayer.getServer();
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeUUID(uuid);
        byteBuf.writeString(server.getName());
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.uuid = byteBuf.readUUID();
        this.server = CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(byteBuf.readString());
    }
}
