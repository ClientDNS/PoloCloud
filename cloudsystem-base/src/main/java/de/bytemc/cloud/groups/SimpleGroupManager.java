package de.bytemc.cloud.groups;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.impl.AbstractGroupManager;

import java.util.stream.Collectors;

public class SimpleGroupManager extends AbstractGroupManager {

    public SimpleGroupManager(){
        getAllCachedServiceGroups().addAll(Base.getInstance().getDatabaseManager().getDatabase().getAllServiceGroups());
        CloudAPI.getInstance().getLoggerProvider().logMessage("§7Loading following groups: §b" +
            String.join(", ", getAllCachedServiceGroups().stream().map(it -> it.getGroup()).collect(Collectors.joining())));
    }



}
