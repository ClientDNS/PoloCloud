package de.bytemc.cloud.api.network.packets.group;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.network.packets.PacketHelper;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ServiceGroupCacheUpdatePacket implements IPacket {

    private List<IServiceGroup> groups;

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(this.groups.size());
        this.groups.forEach(it -> PacketHelper.writeServiceGroup(byteBuf, it, this));
    }

    @Override
    public void read(ByteBuf byteBuf) {
        int amount = byteBuf.readInt();
        this.groups = Lists.newArrayList();
        for (int i = 0; i < amount; i++) {
            this.groups.add(PacketHelper.readServiceGroup(byteBuf, this));
        }
    }

}
