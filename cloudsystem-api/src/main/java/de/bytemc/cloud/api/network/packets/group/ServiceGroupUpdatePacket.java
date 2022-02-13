package de.bytemc.cloud.api.network.packets.group;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class ServiceGroupUpdatePacket implements IPacket {

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

    public ServiceGroupUpdatePacket(final IServiceGroup serviceGroup) {
        this.name = serviceGroup.getName();
        this.node = serviceGroup.getNode();
        this.template = serviceGroup.getTemplate();
        this.motd = serviceGroup.getMotd();
        this.memory = serviceGroup.getMemory();
        this.minOnlineService = serviceGroup.getMinOnlineService();
        this.maxOnlineService = serviceGroup.getMaxOnlineService();
        this.defaultMaxPlayers = serviceGroup.getDefaultMaxPlayers();
        this.gameServerVersion = serviceGroup.getGameServerVersion();
        this.fallback = serviceGroup.isFallbackGroup();
        this.maintenance = serviceGroup.isMaintenance();
    }

    @Override
    public void read(final NetworkByteBuf byteBuf) {
        this.name = byteBuf.readString();
        this.node = byteBuf.readString();
        this.template = byteBuf.readString();
        this.motd = byteBuf.readString();
        this.memory = byteBuf.readInt();
        this.minOnlineService = byteBuf.readInt();
        this.maxOnlineService = byteBuf.readInt();
        this.defaultMaxPlayers = byteBuf.readInt();
        this.gameServerVersion = GameServerVersion.values()[byteBuf.readInt()];
        this.fallback = byteBuf.readBoolean();
        this.maintenance = byteBuf.readBoolean();
    }

    @Override
    public void write(final NetworkByteBuf byteBuf) {
        byteBuf.writeString(this.name);
        byteBuf.writeString(this.node);
        byteBuf.writeString(this.template);
        byteBuf.writeString(this.motd);
        byteBuf.writeInt(this.memory);
        byteBuf.writeInt(this.minOnlineService);
        byteBuf.writeInt(this.maxOnlineService);
        byteBuf.writeInt(this.defaultMaxPlayers);
        byteBuf.writeInt(this.gameServerVersion.ordinal());
        byteBuf.writeBoolean(this.fallback);
        byteBuf.writeBoolean(this.maintenance);
    }

}
