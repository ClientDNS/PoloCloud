package de.bytemc.cloud.api.network.packets.services;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.network.packets.PacketHelper;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServiceCacheUpdatePacket implements IPacket {

    private List<IService> allCachedServices;

    @Override
    public void write(NetworkByteBuf byteBuf) {
        byteBuf.writeInt(this.allCachedServices.size());
        this.allCachedServices.forEach(it -> PacketHelper.writeService(it, byteBuf));
    }

    @Override
    public void read(NetworkByteBuf byteBuf) {
        this.allCachedServices = Lists.newArrayList();
        int serviceAmount = byteBuf.readInt();

        for (int i = 0; i < serviceAmount; i++) {
            this.allCachedServices.add(PacketHelper.readService(byteBuf));
        }
    }

}
