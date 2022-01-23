package de.bytemc.cloud.api.groups.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.groups.IServiceGroup;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class AbstractGroupManager implements IGroupManager {

    private final List<IServiceGroup> allCachedServiceGroups = Lists.newArrayList();

    @Override
    public void addServiceGroup(IServiceGroup serviceGroup) {
        allCachedServiceGroups.add(serviceGroup);
    }

    public void removeServiceGroup(IServiceGroup serviceGroup) {
        allCachedServiceGroups.remove(serviceGroup);
    }

}
