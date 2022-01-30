package de.bytemc.cloud.api.groups;

import de.bytemc.cloud.api.versions.GameServerVersion;

public interface IServiceGroup {

    String getName();

    /**
     * @return the name of the group
     * @deprecated use getName()
     */
    @Deprecated(forRemoval = true)
    String getGroup();

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
