package de.polocloud.wrapper.group;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.groups.impl.AbstractGroupManager;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.group.ServiceGroupCacheUpdatePacket;
import de.polocloud.api.network.packet.group.ServiceGroupUpdatePacket;
import de.polocloud.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

public final class GroupManager extends AbstractGroupManager {

    public GroupManager() {
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceGroupCacheUpdatePacket.class, (ctx, packet) -> this.setAllCachedServiceGroups(packet.getGroups()));
    }

    @Override
    public void updateServiceGroup(@NotNull IServiceGroup serviceGroup) {
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new ServiceGroupUpdatePacket(serviceGroup), QueryPacket.QueryState.FIRST_RESPONSE));
    }

}
