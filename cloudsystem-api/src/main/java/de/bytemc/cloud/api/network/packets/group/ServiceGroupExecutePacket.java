package de.bytemc.cloud.api.network.packets.group;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.ServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class ServiceGroupExecutePacket implements IPacket {

    private IServiceGroup group;
    private executor executorType;

    private enum executor {
        REMOVE, CREATE;
    }

    public ServiceGroupExecutePacket(IServiceGroup group, executor executorType) {
        this.group = group;
        this.executorType = executorType;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        writeString(byteBuf, group.getGroup());
        writeString(byteBuf, group.getTemplate());
        writeString(byteBuf, group.getNode());

        byteBuf.writeInt(group.getMemory());
        byteBuf.writeInt(group.getMinOnlineService());
        byteBuf.writeInt(group.getMaxOnlineService());

        byteBuf.writeBoolean(group.isStaticService());
        byteBuf.writeInt(group.getGameServerVersion().ordinal());

        byteBuf.writeInt(executorType.ordinal());
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.group = new ServiceGroup(readString(byteBuf), readString(byteBuf), readString(byteBuf), byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt(), byteBuf.readBoolean(), GameServerVersion.values()[byteBuf.readInt()]);
        this.executorType = executor.values()[byteBuf.readInt()];
    }
}
