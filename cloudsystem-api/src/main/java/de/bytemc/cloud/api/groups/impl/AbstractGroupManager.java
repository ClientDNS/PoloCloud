package de.bytemc.cloud.api.groups.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceUpdatePacket;
import de.bytemc.cloud.api.services.IService;
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
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceGroupUpdatePacket.class, (ctx, packet) -> {
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
