package de.bytemc.cloud.api.groups;

import java.util.List;
import java.util.stream.Collectors;

public interface IGroupManager {

    List<IServiceGroup> getAllCachedServiceGroups();

    void addServiceGroup(IServiceGroup serviceGroup);

    void removeServiceGroup(IServiceGroup serviceGroup);

    default IServiceGroup getServiceGroupByNameOrNull(String name) {
        return getAllCachedServiceGroups().stream().filter(it -> it.getGroup().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    default boolean isServiceGroupExists(String group) {
        return getServiceGroupByNameOrNull(group) != null;
    }

    default List<IServiceGroup> getServiceGroup(String node){
        return getAllCachedServiceGroups().stream().filter(it -> it.getNode().equalsIgnoreCase(node)).collect(Collectors.toList());
    }

}
