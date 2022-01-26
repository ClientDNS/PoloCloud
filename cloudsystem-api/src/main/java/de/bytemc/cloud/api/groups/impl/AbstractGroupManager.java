package de.bytemc.cloud.api.groups.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.groups.IServiceGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public abstract class AbstractGroupManager implements IGroupManager {

    private List<IServiceGroup> allCachedServiceGroups = Lists.newArrayList();

    @Override
    public void addServiceGroup(IServiceGroup serviceGroup) {
        allCachedServiceGroups.add(serviceGroup);
    }

    public void removeServiceGroup(IServiceGroup serviceGroup) {
        allCachedServiceGroups.remove(serviceGroup);
    }

}
