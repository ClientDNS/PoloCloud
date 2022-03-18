package de.polocloud.database;

import de.polocloud.api.groups.ServiceGroup;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CloudDatabaseProvider {

    void addGroup(@NotNull ServiceGroup serviceGroup);

    void removeGroup(@NotNull ServiceGroup serviceGroup);

    List<ServiceGroup> getAllServiceGroups();

    void updateGroupProperty(@NotNull String group, @NotNull String property, @NotNull Object value);

    void disconnect();

}
