package de.bytemc.cloud.api.network.packets.group;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.network.packets.PacketHelper;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ServiceGroupExecutePacket implements IPacket {

    private IServiceGroup group;
    private executor executorType;

    public enum executor {
        REMOVE, CREATE
    }

    public ServiceGroupExecutePacket(IServiceGroup group, executor executorType) {
        this.group = group;
        this.executorType = executorType;
    }

    @Override
    public void write(NetworkByteBuf byteBuf) {
        PacketHelper.writeServiceGroup(byteBuf, group);
        byteBuf.writeInt(executorType.ordinal());
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.group = PacketHelper.readServiceGroup(byteBuf);
        this.executorType = executor.values()[byteBuf.readInt()];
    }

}
