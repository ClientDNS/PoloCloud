package de.bytemc.cloud.api.network.packets.group;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.ServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @Getter @AllArgsConstructor
public class ServiceGroupCacheUpdatePacket implements IPacket {

    private List<IServiceGroup> groups;

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(groups.size());
        groups.forEach(it -> writeServiceGroup(byteBuf, it));
    }

    @Override
    public void read(ByteBuf byteBuf) {
        int amount = byteBuf.readInt();
        groups = Lists.newArrayList();
        for (int i = 0; i < amount; i++) {
            groups.add(readServiceGroup(byteBuf));
        }
    }

    public void writeServiceGroup(ByteBuf byteBuf, IServiceGroup group) {
        writeString(byteBuf, group.getGroup());
        writeString(byteBuf, group.getTemplate());
        writeString(byteBuf, group.getNode());

        byteBuf.writeInt(group.getMemory());
        byteBuf.writeInt(group.getMinOnlineService());
        byteBuf.writeInt(group.getMaxOnlineService());

        byteBuf.writeBoolean(group.isStaticService());
        byteBuf.writeInt(group.getGameServerVersion().ordinal());
    }

    public IServiceGroup readServiceGroup(ByteBuf byteBuf){
        return new ServiceGroup(readString(byteBuf), readString(byteBuf), readString(byteBuf), byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt(), byteBuf.readBoolean(), GameServerVersion.values()[byteBuf.readInt()]);
    }

}
