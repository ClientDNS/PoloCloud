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
    private boolean staticService, fallbackGroup;
    private GameServerVersion gameServerVersion;


}
