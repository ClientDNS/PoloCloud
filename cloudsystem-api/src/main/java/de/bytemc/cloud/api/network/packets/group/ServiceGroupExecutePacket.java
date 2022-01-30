package de.bytemc.cloud.api.network.packets.group;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.ServiceGroup;
import de.bytemc.cloud.api.network.packets.PacketHelper;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ServiceGroupExecutePacket implements IPacket {

    private IServiceGroup group;
    private executor executorType;

    public enum executor {
        REMOVE, CREATE;
    }

    public ServiceGroupExecutePacket(IServiceGroup group, executor executorType) {
        this.group = group;
        this.executorType = executorType;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        this.writeString(byteBuf, this.group.getName());
        this.writeString(byteBuf, this.group.getTemplate());
        this.writeString(byteBuf, this.group.getNode());

        byteBuf.writeInt(this.group.getMemory());
        byteBuf.writeInt(this.group.getMinOnlineService());
        byteBuf.writeInt(this.group.getMaxOnlineService());

        byteBuf.writeBoolean(this.group.isStaticService());
        byteBuf.writeInt(this.group.getGameServerVersion().ordinal());

        byteBuf.writeInt(this.executorType.ordinal());
        PacketHelper.writeServiceGroup(byteBuf, group, this);
        byteBuf.writeInt(executorType.ordinal());
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.group = PacketHelper.readServiceGroup(byteBuf, this);
        this.executorType = executor.values()[byteBuf.readInt()];
    }

}
