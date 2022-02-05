package de.bytemc.cloud.wrapper.groups;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.AbstractGroupManager;
import de.bytemc.cloud.api.network.packets.QueryPacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupCacheUpdatePacket;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupUpdatePacket;
import de.bytemc.cloud.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

public final class GroupManager extends AbstractGroupManager {

    public GroupManager() {
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceGroupCacheUpdatePacket.class, (ctx, packet) ->
            this.setAllCachedServiceGroups(packet.getGroups()));
    }

    @Override
    public void updateServiceGroup(@NotNull IServiceGroup serviceGroup) {
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new ServiceGroupUpdatePacket(serviceGroup), QueryPacket.QueryState.FIRST_RESPONSE));
    }

}
