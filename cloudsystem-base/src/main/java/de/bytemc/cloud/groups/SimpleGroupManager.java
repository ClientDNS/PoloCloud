package de.bytemc.cloud.groups;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.AbstractGroupManager;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupExecutePacket;
import de.bytemc.cloud.database.IDatabase;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class SimpleGroupManager extends AbstractGroupManager {

    private final IDatabase database;

    public SimpleGroupManager() {
        this.database = Base.getInstance().getDatabaseManager().getDatabase();

        // loading all database groups
        this.getAllCachedServiceGroups().addAll(this.database.getAllServiceGroups());

        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceGroupExecutePacket.class, (ctx, packet) -> {
            if (packet.getExecutorType().equals(ServiceGroupExecutePacket.executor.CREATE)) {
                getAllCachedServiceGroups().add(packet.getGroup());
                Base.getInstance().getGroupTemplateService().createTemplateFolder(packet.getGroup());
                Base.getInstance().getQueueService().checkForQueue();
            } else {
                this.getAllCachedServiceGroups().remove(packet.getGroup());
            }
        });
        CloudAPI.getInstance().getLoggerProvider().logMessage("§7Loading following groups: §b"
            + this.getAllCachedServiceGroups().stream().map(IServiceGroup::getName).collect(Collectors.joining("§7, §b")));
    }

    @Override
    public void addServiceGroup(final @NotNull IServiceGroup serviceGroup) {
        this.database.addGroup(serviceGroup);
        Base.getInstance().getNode().sendPacketToAll(new ServiceGroupExecutePacket(serviceGroup, ServiceGroupExecutePacket.executor.CREATE));
        super.addServiceGroup(serviceGroup);
    }


    @Override
    public void removeServiceGroup(final @NotNull IServiceGroup serviceGroup) {
        this.database.removeGroup(serviceGroup);
        Base.getInstance().getNode().sendPacketToAll(new ServiceGroupExecutePacket(serviceGroup, ServiceGroupExecutePacket.executor.REMOVE));
        super.removeServiceGroup(serviceGroup);
    }
}
