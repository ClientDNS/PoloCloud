package de.polocloud.api.network.packet.init;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.groups.impl.AbstractGroupManager;
import de.polocloud.api.player.CloudPlayer;
import de.polocloud.api.player.impl.AbstractPlayerManager;
import de.polocloud.api.service.CloudService;
import de.polocloud.network.packet.NetworkBuf;
import de.polocloud.network.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@NoArgsConstructor
public class CacheInitPacket implements Packet {

    private List<ServiceGroup> groups;
    private List<CloudService> services;
    private List<CloudPlayer> players;

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        byteBuf.writeInt(this.groups.size());
        this.groups.forEach(group -> group.write(byteBuf));
        byteBuf.writeInt(this.services.size());
        this.services.forEach(service -> service.write(byteBuf));
        byteBuf.writeInt(this.players.size());
        this.players.forEach(player -> player.write(byteBuf));
    }

    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        this.groups = new ArrayList<>();
        final var groupSize = byteBuf.readInt();
        for (int i = 0; i < groupSize; i++) {
            this.groups.add(ServiceGroup.read(byteBuf));
        }

        ((AbstractGroupManager) CloudAPI.getInstance().getGroupManager()).setAllCachedServiceGroups(this.groups);

        this.services = new ArrayList<>();
        final var serviceSize = byteBuf.readInt();
        for (int i = 0; i < serviceSize; i++) {
            this.services.add(CloudService.read(byteBuf));
        }

        CloudAPI.getInstance().getServiceManager().setAllCachedServices(this.services);

        final var players = new ConcurrentHashMap<UUID, CloudPlayer>();
        final var playerSize = byteBuf.readInt();
        for (int i = 0; i < playerSize; i++) {
            final var cloudPlayer = CloudPlayer.read(byteBuf);
            players.put(cloudPlayer.getUniqueId(), cloudPlayer);
        }

        ((AbstractPlayerManager) CloudAPI.getInstance().getPlayerManager()).setPlayers(players);
    }

}
