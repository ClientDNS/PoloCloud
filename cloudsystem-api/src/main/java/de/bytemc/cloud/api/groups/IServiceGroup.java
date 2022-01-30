package de.bytemc.cloud.api.groups;

import de.bytemc.cloud.api.versions.GameServerVersion;

public interface IServiceGroup {

    String getName();

    String getTemplate();

    String getNode();

    int getMemory();

    void setMemory(final int memory);

    int getMinOnlineService();

    void setMinOnlineService(final int minOnlineService);

    int getMaxOnlineService();

    void setMaxOnlineService(final int maxOnlineService);

    boolean isStaticService();

    void setStatic(final boolean b);

    GameServerVersion getGameServerVersion();

    void setGameVersion(final GameServerVersion gameServerVersion);

}
