package de.polocloud.database;

import de.polocloud.api.groups.IServiceGroup;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICloudDatabaseProvider {

    void addGroup(@NotNull IServiceGroup serviceGroup);

    void removeGroup(@NotNull IServiceGroup serviceGroup);

    List<IServiceGroup> getAllServiceGroups();

}
