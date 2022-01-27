package de.bytemc.cloud.api.groups;

import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.versions.GameServerVersion;

public interface IServiceGroup {

    String getGroup();

    String getTemplate();

    String getNode();

    int getMemory();

    int getMinOnlineService();

    int getMaxOnlineService();

    boolean isStaticService();

    GameServerVersion getGameServerVersion();

}
