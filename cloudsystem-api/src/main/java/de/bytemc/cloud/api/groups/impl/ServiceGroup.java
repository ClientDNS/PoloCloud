package de.bytemc.cloud.api.groups.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceGroup implements IServiceGroup {

    private String name, template, node;
    private int memory, defaultMaxPlayers, minOnlineService, maxOnlineService;
    private boolean staticService, fallbackGroup;
    private GameServerVersion gameServerVersion;

    @Override
    public void update() {
        CloudAPI.getInstance().getGroupManager().updateServiceGroup(this);
    }

}
