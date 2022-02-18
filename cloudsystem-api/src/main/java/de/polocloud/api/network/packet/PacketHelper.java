package de.polocloud.api.network.packet;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.groups.impl.ServiceGroup;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.impl.SimpleCloudPlayer;
import de.polocloud.api.service.IService;
import de.polocloud.api.service.impl.SimpleService;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.api.service.utils.ServiceVisibility;
import de.polocloud.api.version.GameServerVersion;
import de.polocloud.network.packet.NetworkBuf;

public class PacketHelper {

    public static void writeService(NetworkBuf byteBuf, IService service) {
        byteBuf.writeString(service.getGroup().getName());
        byteBuf.writeInt(service.getServiceId());
        byteBuf.writeString(service.getNode());
        byteBuf.writeInt(service.getPort());
        byteBuf.writeString(service.getHostName());
        byteBuf.writeInt(service.getMaxPlayers());
        byteBuf.writeInt(service.getServiceState().ordinal());
        byteBuf.writeInt(service.getServiceVisibility().ordinal());
        byteBuf.writeString(service.getMotd());
    }

    public static IService readService(NetworkBuf byteBuf) {
        return new SimpleService(byteBuf.readString(), byteBuf.readInt(), byteBuf.readString(),
            byteBuf.readInt(), byteBuf.readString(), byteBuf.readInt(), ServiceState.values()[byteBuf.readInt()],
            ServiceVisibility.values()[byteBuf.readInt()], byteBuf.readString());
    }

    public static void writeCloudPlayer(NetworkBuf byteBuf, ICloudPlayer cloudPlayer) {
        byteBuf.writeUUID(cloudPlayer.getUniqueId());
        byteBuf.writeString(cloudPlayer.getUsername());
        byteBuf.writeString(cloudPlayer.getProxyServer().getName());
        byteBuf.writeString(cloudPlayer.getServer().getName());
    }

    public static ICloudPlayer readCloudPlayer(NetworkBuf byteBuf) {
        SimpleCloudPlayer simpleCloudPlayer = new SimpleCloudPlayer(byteBuf.readUUID(), byteBuf.readString(),
            CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(byteBuf.readString()));
        simpleCloudPlayer.setServer(CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(byteBuf.readString()));
        return simpleCloudPlayer;
    }

    public static void writeServiceGroup(NetworkBuf byteBuf, IServiceGroup group) {
        byteBuf.writeString(group.getName());
        byteBuf.writeString(group.getTemplate());
        byteBuf.writeString(group.getNode());
        byteBuf.writeString(group.getMotd());
        byteBuf.writeInt(group.getMemory());
        byteBuf.writeInt(group.getDefaultMaxPlayers());
        byteBuf.writeInt(group.getMinOnlineService());
        byteBuf.writeInt(group.getMaxOnlineService());

        byteBuf.writeBoolean(group.isStatic());
        byteBuf.writeBoolean(group.isFallbackGroup());
        byteBuf.writeBoolean(group.isMaintenance());
        byteBuf.writeBoolean(group.isAutoUpdating());
        byteBuf.writeInt(group.getGameServerVersion().ordinal());
    }

    public static IServiceGroup readServiceGroup(NetworkBuf byteBuf) {
        return new ServiceGroup(byteBuf.readString(),
            byteBuf.readString(),
            byteBuf.readString(),
            byteBuf.readString(),
            byteBuf.readInt(),
            byteBuf.readInt(),
            byteBuf.readInt(),
            byteBuf.readInt(),
            byteBuf.readBoolean(),
            byteBuf.readBoolean(),
            byteBuf.readBoolean(),
            byteBuf.readBoolean(),
            GameServerVersion.values()[byteBuf.readInt()]);
    }

}
