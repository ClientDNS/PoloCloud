package de.polocloud.api.network.packet.group;

import com.google.common.collect.Lists;
import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.network.packet.PacketHelper;
import de.polocloud.network.packet.Packet;
import de.polocloud.network.packet.NetworkBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ServiceGroupCacheUpdatePacket implements Packet {

    private List<IServiceGroup> groups;

    @Override
    public void write(@NotNull NetworkBuf byteBuf) {
        byteBuf.writeInt(this.groups.size());
        this.groups.forEach(it -> PacketHelper.writeServiceGroup(byteBuf, it));
    }

    @Override
    public void read(@NotNull NetworkBuf byteBuf) {
        int amount = byteBuf.readInt();
        this.groups = Lists.newArrayList();
        for (int i = 0; i < amount; i++) {
            this.groups.add(PacketHelper.readServiceGroup(byteBuf));
        }
    }

}
