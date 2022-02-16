package de.polocloud.base.database;

import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.network.promise.ICommunicationPromise;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IDatabase {

    void connect(@NotNull DatabaseConfiguration databaseConfiguration);

    @NotNull ICommunicationPromise<Void> disconnect();

    List<IServiceGroup> getAllServiceGroups();

    void addGroup(@NotNull IServiceGroup serviceGroup);

    void removeGroup(@NotNull IServiceGroup serviceGroup);


}
