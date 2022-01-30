package de.bytemc.cloud.api.groups.impl;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceGroup implements IServiceGroup {

    private String name, template, node;
    private int memory, minOnlineService, maxOnlineService;
    private boolean staticService;
    private GameServerVersion gameServerVersion;

    @Override
    public String getGroup() {
        return this.name;
    }

    @Override
    public void setMemory(final int memory) {
        this.memory = memory;
    }

    @Override
    public void setMinOnlineService(final int minOnlineService) {
        this.minOnlineService = minOnlineService;
        // TODO update
    }

    @Override
    public void setMaxOnlineService(final int maxOnlineService) {
        this.maxOnlineService = maxOnlineService;
        // TODO update
    }

    @Override
    public void setStatic(final boolean b) {
        this.staticService = b;
        // TODO update
    }

    @Override
    public void setGameVersion(final GameServerVersion gameServerVersion) {
        this.gameServerVersion = gameServerVersion;
        // TODO update
    }

}
