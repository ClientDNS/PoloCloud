package de.polocloud.api.network.packet.group;

import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.version.GameServerVersion;
import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.NetworkBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
public final class ServiceGroupUpdatePacket implements Packet {

    private String name;
    private String node;
    private String template;
    private String motd;
    private int memory;
    private int minOnlineService;
    private int maxOnlineService;
    private int defaultMaxPlayers;
    private GameServerVersion gameServerVersion;
    private boolean fallback;
    private boolean maintenance;

    public ServiceGroupUpdatePacket(final ServiceGroup serviceGroup) {
        this.name = serviceGroup.getName();
        this.node = serviceGroup.getNode();
        this.template = serviceGroup.getTemplate();
        this.motd = serviceGroup.getMotd();
        this.memory = serviceGroup.getMaxMemory();
        this.minOnlineService = serviceGroup.getMinOnlineService();
        this.maxOnlineService = serviceGroup.getMaxOnlineService();
        this.defaultMaxPlayers = serviceGroup.getDefaultMaxPlayers();
        this.gameServerVersion = serviceGroup.getGameServerVersion();
        this.fallback = serviceGroup.isFallbackGroup();
        this.maintenance = serviceGroup.isMaintenance();
    }

    @Override
    public void read(final @NotNull NetworkBuf byteBuf) {
        this.name = byteBuf.readString();
        this.node = byteBuf.readString();
        this.template = byteBuf.readString();
        this.motd = byteBuf.readString();
        this.memory = byteBuf.readInt();
        this.minOnlineService = byteBuf.readInt();
        this.maxOnlineService = byteBuf.readInt();
        this.defaultMaxPlayers = byteBuf.readInt();
        this.gameServerVersion = GameServerVersion.VERSIONS.values().stream().toList().get(byteBuf.readInt());
        this.fallback = byteBuf.readBoolean();
        this.maintenance = byteBuf.readBoolean();
    }

    @Override
    public void write(final @NotNull NetworkBuf byteBuf) {
        byteBuf.writeString(this.name);
        byteBuf.writeString(this.node);
        byteBuf.writeString(this.template);
        byteBuf.writeString(this.motd);
        byteBuf.writeInt(this.memory);
        byteBuf.writeInt(this.minOnlineService);
        byteBuf.writeInt(this.maxOnlineService);
        byteBuf.writeInt(this.defaultMaxPlayers);
        byteBuf.writeInt(GameServerVersion.VERSIONS.values().stream().toList().indexOf(this.gameServerVersion));
        byteBuf.writeBoolean(this.fallback);
        byteBuf.writeBoolean(this.maintenance);
    }

}
