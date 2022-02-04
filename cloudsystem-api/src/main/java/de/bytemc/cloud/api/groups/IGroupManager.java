package de.bytemc.cloud.api.groups;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public interface IGroupManager {

    /**
     * gets all cached service groups
     * @return the cached service groups
     */
    List<IServiceGroup> getAllCachedServiceGroups();

    /**
     * adds a service group
     * @param serviceGroup the service group to add
     */
    void addServiceGroup(final @NotNull IServiceGroup serviceGroup);

    /**
     * removes a service group
     * @param serviceGroup the service group to remove
     */
    void removeServiceGroup(final @NotNull IServiceGroup serviceGroup);

    /**
     * gets a service group
     * @param name the name of the service group
     * @return the service group or null when it not exists
     */
    default @Nullable IServiceGroup getServiceGroupByNameOrNull(final @NotNull String name) {
        return getAllCachedServiceGroups().stream().filter(it -> it.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    /**
     * checks if a group exists
     * @param name the name of the group
     * @return true if the group exists
     */
    default boolean isServiceGroupExists(final @NotNull String name) {
        return this.getServiceGroupByNameOrNull(name) != null;
    }

    /**
     * gets all service groups by node
     * @param node the node
     * @return all services of the node
     */
    default List<IServiceGroup> getServiceGroup(final @NotNull String node) {
        return this.getAllCachedServiceGroups().stream()
            .filter(it -> it.getNode().equalsIgnoreCase(node))
            .collect(Collectors.toList());
    }

    /**
     * update a group
     * @param serviceGroup the group to update
     */
    void updateServiceGroup(final @NotNull IServiceGroup serviceGroup);

}
