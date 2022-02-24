package de.polocloud.base.group;

import de.polocloud.api.groups.impl.SimpleServiceGroup;
import de.polocloud.api.version.GameServerVersion;

public final class DefaultGroup extends SimpleServiceGroup {

    public DefaultGroup(String node, String group, int memory, boolean staticService, GameServerVersion gameServerVersion) {
        super(group, group, node, "A default service", memory, 100, 1,
            -1, staticService, false, false, true, gameServerVersion);
    }

}
