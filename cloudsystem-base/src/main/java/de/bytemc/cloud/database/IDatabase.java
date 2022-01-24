package de.bytemc.cloud.database;

import de.bytemc.cloud.api.groups.IServiceGroup;

import java.util.List;

public interface IDatabase {

    void connect();

    void disconnect();

    List<IServiceGroup> getAllServiceGroups();

    void addGroup(IServiceGroup serviceGroup);

    void removeGroup(IServiceGroup serviceGroup);


}
