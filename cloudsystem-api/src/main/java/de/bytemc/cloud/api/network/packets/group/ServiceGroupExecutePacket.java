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
        PacketHelper.writeServiceGroup(byteBuf, group, this);
        byteBuf.writeInt(executorType.ordinal());
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.group = PacketHelper.readServiceGroup(byteBuf, this);
        this.executorType = executor.values()[byteBuf.readInt()];
    }

}
