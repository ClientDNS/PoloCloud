package de.polocloud.api.groups.impl;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.group.CloudServiceGroupUpdateEvent;
import de.polocloud.api.groups.GroupManager;
import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.network.packet.group.ServiceGroupExecutePacket;
import de.polocloud.api.network.packet.group.ServiceGroupUpdatePacket;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class AbstractGroupManager implements GroupManager {

    private List<ServiceGroup> allCachedServiceGroups = new ArrayList<>();

    public AbstractGroupManager() {
        CloudAPI.getInstance().getPacketHandler().registerPacketListener(ServiceGroupUpdatePacket.class, (channelHandlerContext, packet) ->
            this.getServiceGroupByName(packet.getName()).ifPresent(group -> {
                group.setNode(packet.getNode());
                group.setTemplate(packet.getTemplate());
                group.setMotd(packet.getMotd());
                group.setMaxMemory(packet.getMemory());
                group.setMinOnlineService(packet.getMinOnlineService());
                group.setMaxOnlineService(packet.getMaxOnlineService());
                group.setDefaultMaxPlayers(packet.getDefaultMaxPlayers());
                group.setGameServerVersion(packet.getGameServerVersion());
                group.setFallbackGroup(packet.isFallback());
                group.setMaintenance(packet.isMaintenance());
                CloudAPI.getInstance().getEventHandler().call(new CloudServiceGroupUpdateEvent(Objects.requireNonNull(
                    CloudAPI.getInstance().getGroupManager().getServiceGroupByNameOrNull(packet.getName()))));
            }));

        CloudAPI.getInstance().getPacketHandler().registerPacketListener(ServiceGroupExecutePacket.class, (channelHandlerContext, packet) -> {
            if (packet.getExecutorType().equals(ServiceGroupExecutePacket.Executor.CREATE)) {
                addServiceGroup(packet.getGroup());
            } else {
                this.getAllCachedServiceGroups().remove(packet.getGroup());
            }
        });
    }

    @Override
    public void addServiceGroup(final @NotNull ServiceGroup serviceGroup) {
        this.allCachedServiceGroups.add(serviceGroup);
    }

    public void removeServiceGroup(final @NotNull ServiceGroup serviceGroup) {
        this.allCachedServiceGroups.remove(serviceGroup);
    }

}
