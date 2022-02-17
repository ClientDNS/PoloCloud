package de.polocloud.api.network.packet.init;

import com.google.common.collect.Lists;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.groups.impl.AbstractGroupManager;
import de.polocloud.api.network.packet.PacketHelper;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.impl.AbstractPlayerManager;
import de.polocloud.api.service.IService;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@NoArgsConstructor
public class CacheInitPacket implements IPacket {

    private List<IServiceGroup> groups;
    private List<IService> services;
    private List<ICloudPlayer> players;

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeInt(this.groups.size());
        this.groups.forEach(group -> PacketHelper.writeServiceGroup(byteBuf, group));
        byteBuf.writeInt(this.services.size());
        this.services.forEach(service -> PacketHelper.writeService(byteBuf, service));
        byteBuf.writeInt(this.players.size());
        this.players.forEach(player -> PacketHelper.writeCloudPlayer(byteBuf, player));
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.groups = Lists.newArrayList();
        final var groupSize = byteBuf.readInt();
        for (int i = 0; i < groupSize; i++) {
            this.groups.add(PacketHelper.readServiceGroup(byteBuf));
        }

        ((AbstractGroupManager) CloudAPI.getInstance().getGroupManager()).setAllCachedServiceGroups(this.groups);

        this.services = Lists.newArrayList();
        final var serviceSize = byteBuf.readInt();
        for (int i = 0; i < serviceSize; i++) {
            this.services.add(PacketHelper.readService(byteBuf));
        }

        CloudAPI.getInstance().getServiceManager().setAllCachedServices(this.services);

        final Map<UUID, ICloudPlayer> players = new ConcurrentHashMap<>();
        final var playerSize = byteBuf.readInt();
        for (int i = 0; i < playerSize; i++) {
            final var cloudPlayer = PacketHelper.readCloudPlayer(byteBuf);
            players.put(cloudPlayer.getUniqueId(), cloudPlayer);
        }

        ((AbstractPlayerManager) CloudAPI.getInstance().getPlayerManager()).setPlayers(players);
    }

}
