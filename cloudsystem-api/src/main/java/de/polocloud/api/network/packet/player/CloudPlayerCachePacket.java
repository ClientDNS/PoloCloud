package de.polocloud.api.network.packet.player;

import com.google.common.collect.Lists;
import de.polocloud.api.network.packet.PacketHelper;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
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
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeInt(cloudPlayers.size());
        for (ICloudPlayer cloudPlayer : cloudPlayers) {
            PacketHelper.writeCloudPlayer(byteBuf, cloudPlayer);
        }
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.cloudPlayers = Lists.newArrayList();

        final int amount = byteBuf.readInt();
        for (int i = 0; i < amount; i++) {
            cloudPlayers.add(PacketHelper.readCloudPlayer(byteBuf));
        }
    }
}
