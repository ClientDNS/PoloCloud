package de.polocloud.base.group;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.groups.impl.AbstractGroupManager;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.group.ServiceGroupExecutePacket;
import de.polocloud.api.network.packet.group.ServiceGroupUpdatePacket;
import de.polocloud.base.Base;
import de.polocloud.database.CloudDatabaseProvider;
import de.polocloud.network.NetworkType;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public final class SimpleGroupManager extends AbstractGroupManager {

    private final CloudDatabaseProvider database;

    public SimpleGroupManager() {
        this.database = Base.getInstance().getDatabaseManager().getProvider();

        // loading all database groups
        this.getAllCachedServiceGroups().addAll(this.database.getAllServiceGroups());

        CloudAPI.getInstance().getLogger().log("§7Loading following groups: §b"
            + this.getAllCachedServiceGroups().stream().map(ServiceGroup::getName).collect(Collectors.joining("§7, §b")));
    }

    @Override
    public void addServiceGroup(final @NotNull ServiceGroup serviceGroup) {
        this.database.addGroup(serviceGroup);
        Base.getInstance().getNode().sendPacketToAll(new ServiceGroupExecutePacket(serviceGroup, ServiceGroupExecutePacket.Executor.CREATE));
        super.addServiceGroup(serviceGroup);
    }


    @Override
    public void removeServiceGroup(final @NotNull ServiceGroup serviceGroup) {
        this.database.removeGroup(serviceGroup);
        Base.getInstance().getNode().sendPacketToAll(new ServiceGroupExecutePacket(serviceGroup, ServiceGroupExecutePacket.Executor.REMOVE));
        super.removeServiceGroup(serviceGroup);
    }

    @Override
    public void updateServiceGroup(@NotNull ServiceGroup serviceGroup) {
        final var packet = new ServiceGroupUpdatePacket(serviceGroup);
        // update all other nodes and this service groups
        Base.getInstance().getNode().sendPacketToType(new QueryPacket(packet, QueryPacket.QueryState.SECOND_RESPONSE), NetworkType.NODE);
        // update own service group caches
        Base.getInstance().getNode().sendPacketToType(packet, NetworkType.WRAPPER);
    }

}
