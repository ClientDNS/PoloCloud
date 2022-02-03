package de.bytemc.cloud.database;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.network.promise.ICommunicationPromise;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IDatabase {

    void connect();

    @NotNull ICommunicationPromise<Void> disconnect();

    List<IServiceGroup> getAllServiceGroups();

    void addGroup(final @NotNull IServiceGroup serviceGroup);

    void removeGroup(final @NotNull IServiceGroup serviceGroup);


}
