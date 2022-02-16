package de.polocloud.api.network.packet.group;

import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.network.packet.PacketHelper;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
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
