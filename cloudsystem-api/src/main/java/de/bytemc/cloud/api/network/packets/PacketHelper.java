package de.bytemc.cloud.api.network.packets;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.ServiceGroup;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.impl.SimpleCloudPlayer;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.cloud.api.versions.GameServerVersion;
import de.bytemc.network.packets.NetworkByteBuf;

public class PacketHelper {

    public static void writeService(IService service, NetworkByteBuf byteBuf) {
        byteBuf.writeString(service.getServiceGroup().getName());
        byteBuf.writeInt(service.getServiceID());
        byteBuf.writeInt(service.getPort());
        byteBuf.writeString(service.getHostName());
        byteBuf.writeInt(service.getMaxPlayers());
        byteBuf.writeInt(service.getServiceState().ordinal());
        byteBuf.writeInt(service.getServiceVisibility().ordinal());
        byteBuf.writeString(service.getMotd());
    }

    public static IService readService(NetworkByteBuf byteBuf) {
        return new SimpleService(byteBuf.readString(), byteBuf.readInt(), byteBuf.readInt(), byteBuf.readString(), byteBuf.readInt(), ServiceState.values()[byteBuf.readInt()],  ServiceVisibility.values()[byteBuf.readInt()], byteBuf.readString());
    }

    public static void writeCloudPlayer(NetworkByteBuf byteBuf, ICloudPlayer cloudPlayer) {
        byteBuf.writeUUID(cloudPlayer.getUniqueId());
        byteBuf.writeString(cloudPlayer.getUsername());
        byteBuf.writeString(cloudPlayer.getProxyServer().getName());
        byteBuf.writeString(cloudPlayer.getServer().getName());
    }

    public static ICloudPlayer readCloudPlayer(NetworkByteBuf byteBuf){
        SimpleCloudPlayer simpleCloudPlayer = new SimpleCloudPlayer(byteBuf.readUUID(), byteBuf.readString());
        simpleCloudPlayer.setProxyServer(CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(byteBuf.readString()));
        simpleCloudPlayer.setServer(CloudAPI.getInstance().getServiceManager().getServiceByNameOrNull(byteBuf.readString()));
        return simpleCloudPlayer;
    }

    public static void writeServiceGroup(NetworkByteBuf byteBuf, IServiceGroup group) {
        byteBuf.writeString(group.getName());
        byteBuf.writeString(group.getTemplate());
        byteBuf.writeString(group.getNode());
        byteBuf.writeString(group.getMotd());
        byteBuf.writeInt(group.getMemory());
        byteBuf.writeInt(group.getDefaultMaxPlayers());
        byteBuf.writeInt(group.getMinOnlineService());
        byteBuf.writeInt(group.getMaxOnlineService());

        byteBuf.writeBoolean(group.isStaticService());
        byteBuf.writeBoolean(group.isFallbackGroup());
        byteBuf.writeBoolean(group.isMaintenance());
        byteBuf.writeInt(group.getGameServerVersion().ordinal());
    }

    public static IServiceGroup readServiceGroup(NetworkByteBuf byteBuf) {
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
            GameServerVersion.values()[byteBuf.readInt()]);
    }

}
