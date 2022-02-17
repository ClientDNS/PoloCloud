package de.polocloud.wrapper.group;

import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.groups.impl.AbstractGroupManager;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.group.ServiceGroupUpdatePacket;
import de.polocloud.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

public final class GroupManager extends AbstractGroupManager {

    @Override
    public void updateServiceGroup(@NotNull IServiceGroup serviceGroup) {
        Wrapper.getInstance().getClient().sendPacket(new QueryPacket(new ServiceGroupUpdatePacket(serviceGroup), QueryPacket.QueryState.FIRST_RESPONSE));
    }

}
