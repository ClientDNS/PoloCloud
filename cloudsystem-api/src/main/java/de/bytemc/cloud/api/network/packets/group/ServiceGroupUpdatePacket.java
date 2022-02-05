package de.bytemc.cloud.api.network.packets.group;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class ServiceGroupUpdatePacket implements IPacket {

    private String name;
    private String node;
    private String template;
    private int memory;
    private int minOnlineService;
    private int maxOnlineService;
    private int defaultMaxPlayers;
    private GameServerVersion gameServerVersion;
    private boolean fallback;

    public ServiceGroupUpdatePacket(final IServiceGroup serviceGroup) {
        this.name = serviceGroup.getName();
        this.node = serviceGroup.getNode();
        this.template = serviceGroup.getTemplate();
        this.memory = serviceGroup.getMemory();
        this.minOnlineService = serviceGroup.getMinOnlineService();
        this.maxOnlineService = serviceGroup.getMaxOnlineService();
        this.defaultMaxPlayers = serviceGroup.getDefaultMaxPlayers();
        this.gameServerVersion = serviceGroup.getGameServerVersion();
        this.fallback = serviceGroup.isFallbackGroup();
    }

    @Override
    public void read(final ByteBuf byteBuf) {
        this.name = this.readString(byteBuf);
        this.node = this.readString(byteBuf);
        this.template = this.readString(byteBuf);
        this.memory = byteBuf.readInt();
        this.minOnlineService = byteBuf.readInt();
        this.maxOnlineService = byteBuf.readInt();
        this.defaultMaxPlayers = byteBuf.readInt();
        this.gameServerVersion = GameServerVersion.values()[byteBuf.readInt()];
        this.fallback = byteBuf.readBoolean();
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        this.writeString(byteBuf, this.name);
        this.writeString(byteBuf, this.node);
        this.writeString(byteBuf, this.template);
        byteBuf.writeInt(this.memory);
        byteBuf.writeInt(this.minOnlineService);
        byteBuf.writeInt(this.maxOnlineService);
        byteBuf.writeInt(this.defaultMaxPlayers);
        byteBuf.writeInt(this.gameServerVersion.ordinal());
        byteBuf.writeBoolean(this.fallback);
    }

}
