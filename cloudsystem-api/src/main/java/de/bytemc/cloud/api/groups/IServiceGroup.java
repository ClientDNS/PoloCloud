package de.bytemc.cloud.api.groups;

import de.bytemc.cloud.api.versions.GameServerVersion;

public interface IServiceGroup {

    String getName();

    String getTemplate();

    String getNode();

    int getMemory();

    int getDefaultMaxPlayers();

    int getMinOnlineService();

    int getMaxOnlineService();

    boolean isStaticService();

    boolean isFallbackGroup();

    GameServerVersion getGameServerVersion();

}
