package de.bytemc.cloud.database;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.network.promise.ICommunicationPromise;

import java.util.List;

public interface IDatabase {

    void connect();

    ICommunicationPromise<Void> disconnect();

    List<IServiceGroup> getAllServiceGroups();

    void addGroup(IServiceGroup serviceGroup);

    void removeGroup(IServiceGroup serviceGroup);


}
