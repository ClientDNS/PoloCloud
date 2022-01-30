package de.bytemc.cloud.api.network.packets;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.ServiceGroup;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.network.packets.IPacket;
import io.netty.buffer.ByteBuf;

public class PacketHelper {

    public static void writeService(IService service, ByteBuf byteBuf, IPacket packet) {
        packet.writeString(byteBuf, service.getServiceGroup().getName());
        byteBuf.writeInt(service.getServiceID());
        byteBuf.writeInt(service.getPort());
        packet.writeString(byteBuf, service.getHostName());
        byteBuf.writeInt(service.getServiceState().ordinal());
    }

    public static IService readService(ByteBuf byteBuf, IPacket packet) {
        return new SimpleService(packet.readString(byteBuf), byteBuf.readInt(), byteBuf.readInt(),
            packet.readString(byteBuf), ServiceState.values()[byteBuf.readInt()]);
    }

    public static void writeServiceGroup(ByteBuf byteBuf, IServiceGroup group, IPacket packet) {
        packet.writeString(byteBuf, group.getName());
        packet.writeString(byteBuf, group.getTemplate());
        packet.writeString(byteBuf, group.getNode());

        byteBuf.writeInt(group.getMemory());
        byteBuf.writeInt(group.getMinOnlineService());
        byteBuf.writeInt(group.getMaxOnlineService());

        byteBuf.writeBoolean(group.isStaticService());
        byteBuf.writeInt(group.getGameServerVersion().ordinal());
    }

    public static IServiceGroup readServiceGroup(ByteBuf byteBuf, IPacket packet) {
        return new ServiceGroup(packet.readString(byteBuf), packet.readString(byteBuf), packet.readString(byteBuf),
            byteBuf.readInt(), byteBuf.readInt(), byteBuf.readInt(), byteBuf.readBoolean(), GameServerVersion.values()[byteBuf.readInt()]);
    }

}
