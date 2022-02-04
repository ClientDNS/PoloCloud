package de.bytemc.cloud.api.groups;

import de.bytemc.cloud.api.versions.GameServerVersion;
import org.jetbrains.annotations.NotNull;

public interface IServiceGroup {

    @NotNull String getName();

    @NotNull String getTemplate();

    void setTemplate(final @NotNull String template);

    @NotNull String getNode();

    void setNode(final @NotNull String node);

    int getMemory();

    void setMemory(final int memory);

    int getDefaultMaxPlayers();

    void setDefaultMaxPlayers(final int defaultMaxPlayers);

    int getMinOnlineService();

    void setMinOnlineService(final int minOnlineService);

    int getMaxOnlineService();

    void setMaxOnlineService(final int maxOnlineService);

    boolean isStaticService();

    boolean isFallbackGroup();

    void setFallbackGroup(final boolean fallbackGroup);

    @NotNull GameServerVersion getGameServerVersion();

    void setGameServerVersion(final @NotNull GameServerVersion gameServerVersion);

    // TODO update in Database
    void update();

}
