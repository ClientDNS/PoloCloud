package de.bytemc.cloud.api.groups;

import de.bytemc.cloud.api.versions.GameServerVersion;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface IServiceGroup {

    @NotNull String getName();

    @NotNull String getTemplate();

    void setTemplate(@NotNull String template);

    @NotNull String getNode();

    void setNode(@NotNull String node);

    int getMemory();

    void setMemory(int memory);

    int getDefaultMaxPlayers();

    void setDefaultMaxPlayers(int defaultMaxPlayers);

    int getMinOnlineService();

    void setMinOnlineService(int minOnlineService);

    int getMaxOnlineService();

    void setMaxOnlineService(int maxOnlineService);

    boolean isStaticService();

    boolean isFallbackGroup();

    void setFallbackGroup(boolean fallbackGroup);

    @NotNull GameServerVersion getGameServerVersion();

    void setGameServerVersion(@NotNull GameServerVersion gameServerVersion);

    void edit(@NotNull Consumer<IServiceGroup> serviceGroupConsumer);

    // TODO update in Database
    void update();

}
