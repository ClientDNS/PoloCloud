package de.bytemc.cloud.api.network.packets.player;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudPlayerUpdateEvent;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CloudPlayerUpdatePacket implements IPacket {

    private UUID uuid;
    private IService server;
    private CloudPlayerUpdateEvent.UpdateReason updateReason;

    public CloudPlayerUpdatePacket(@NotNull ICloudPlayer cloudPlayer, @NotNull CloudPlayerUpdateEvent.UpdateReason updateReason) {
        this.uuid = cloudPlayer.getUniqueId();
        this.server = cloudPlayer.getServer();
        this.updateReason = updateReason;
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeUUID(this.uuid);
        byteBuf.writeString(this.server.getName());
        byteBuf.writeEnum(this.updateReason);
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.uuid = byteBuf.readUUID();
        this.server = CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(byteBuf.readString());
        this.updateReason = byteBuf.readEnum();
    }

}
