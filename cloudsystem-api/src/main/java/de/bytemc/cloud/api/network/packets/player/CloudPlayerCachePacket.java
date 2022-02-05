package de.bytemc.cloud.api.network.packets.player;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.network.packets.PacketHelper;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudPlayerCachePacket implements IPacket {

    private Collection<ICloudPlayer> cloudPlayers;

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(cloudPlayers.size());
        for (ICloudPlayer cloudPlayer : cloudPlayers) {
            PacketHelper.writeCloudPlayer(byteBuf, cloudPlayer, this);
        }
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.cloudPlayers = Lists.newArrayList();

        final int amount = byteBuf.readInt();
        for (int i = 0; i < amount; i++) {
            cloudPlayers.add(PacketHelper.readCloudPlayer(byteBuf, this));
        }
    }
}
