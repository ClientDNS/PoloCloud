package de.bytemc.cloud.api.groups;

import de.bytemc.cloud.api.groups.impl.ServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;

public class DefaultGroup extends ServiceGroup {

    public DefaultGroup(String group, int memory, boolean staticService, GameServerVersion gameServerVersion) {
        super(group, group, "node-1", "A default service", memory, 100, 1, -1, staticService, true, false, gameServerVersion);
    }

}
