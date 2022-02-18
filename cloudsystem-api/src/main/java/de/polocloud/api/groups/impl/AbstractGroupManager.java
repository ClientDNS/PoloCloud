package de.polocloud.api.groups.impl;

import com.google.common.collect.Lists;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.IGroupManager;
import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.network.packet.group.ServiceGroupUpdatePacket;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class AbstractGroupManager implements IGroupManager {

    private List<IServiceGroup> allCachedServiceGroups = Lists.newArrayList();

    public AbstractGroupManager() {
        CloudAPI.getInstance().getPacketHandler().registerPacketListener(ServiceGroupUpdatePacket.class, (channelHandlerContext, packet) -> {
            final IServiceGroup serviceGroup = this.getServiceGroupByNameOrNull(packet.getName());
            Objects.requireNonNull(serviceGroup, "Updated service group is null.");

            serviceGroup.setNode(packet.getNode());
            serviceGroup.setTemplate(packet.getTemplate());
            serviceGroup.setMotd(packet.getMotd());
            serviceGroup.setMemory(packet.getMemory());
            serviceGroup.setMinOnlineService(packet.getMinOnlineService());
            serviceGroup.setMaxOnlineService(packet.getMaxOnlineService());
            serviceGroup.setDefaultMaxPlayers(packet.getDefaultMaxPlayers());
            serviceGroup.setGameServerVersion(packet.getGameServerVersion());
            serviceGroup.setFallbackGroup(packet.isFallback());
        });
    }

    @Override
    public void addServiceGroup(final @NotNull IServiceGroup serviceGroup) {
        this.allCachedServiceGroups.add(serviceGroup);
    }

    public void removeServiceGroup(final @NotNull IServiceGroup serviceGroup) {
        this.allCachedServiceGroups.remove(serviceGroup);
    }

}
