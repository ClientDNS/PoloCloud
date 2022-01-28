package de.bytemc.cloud.groups;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.impl.AbstractGroupManager;
import de.bytemc.cloud.api.network.packets.group.ServiceGroupExecutePacket;
import de.bytemc.cloud.database.IDatabase;
import de.bytemc.cloud.node.BaseNode;

import java.util.stream.Collectors;

public class SimpleGroupManager extends AbstractGroupManager {

    private IDatabase database;

    public SimpleGroupManager(){

        this.database = Base.getInstance().getDatabaseManager().getDatabase();

        //loading all database groups
        getAllCachedServiceGroups().addAll(database.getAllServiceGroups());
        CloudAPI.getInstance().getLoggerProvider().logMessage("§7Loading following groups: §b" + String.join("§7, §b", getAllCachedServiceGroups().stream().map(it -> it.getGroup()).collect(Collectors.toList())));
    }

    @Override
    public void addServiceGroup(IServiceGroup serviceGroup) {
        database.addGroup(serviceGroup);
        Base.getInstance().getNode().sendPacketToAll(new ServiceGroupExecutePacket(serviceGroup, ServiceGroupExecutePacket.executor.CREATE));
        super.addServiceGroup(serviceGroup);
    }


    @Override
    public void removeServiceGroup(IServiceGroup serviceGroup) {
        database.removeGroup(serviceGroup);
        Base.getInstance().getNode().sendPacketToAll(new ServiceGroupExecutePacket(serviceGroup, ServiceGroupExecutePacket.executor.REMOVE));
        super.removeServiceGroup(serviceGroup);
    }
}
