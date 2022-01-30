package de.bytemc.cloud.api.groups;

import de.bytemc.cloud.api.versions.GameServerVersion;
import org.jetbrains.annotations.NotNull;

public interface IServiceGroup {

    /**
     * @return the name of the group
     */
    @NotNull String getName();

    /**
     * @return the template of the group
     */
    @NotNull String getTemplate();

    /**
     * @return the node of the group
     */
    @NotNull String getNode();

    /**
     * @return the max memory of a service
     */
    int getMemory();

    /**
     * sets the max memory of a service
     * @param memory the memory to set
     */
    void setMemory(final int memory);

    /**
     * @return the min online service count
     */
    int getMinOnlineService();

    /**
     * sets the min online service count
     * @param minOnlineService the min online service count to set
     */
    void setMinOnlineService(final int minOnlineService);

    /**
     * @return the max online service count
     */
    int getMaxOnlineService();

    /**
     * sets the max online service count
     * @param maxOnlineService the max online service count to set
     */
    void setMaxOnlineService(final int maxOnlineService);

    /**
     * @return true if the service is static
     */
    boolean isStaticService();

    /**
     * @param b static or not
     */
    void setStatic(final boolean b);

    /**
     * @return the game server version
     */
    @NotNull GameServerVersion getGameServerVersion();

    /**
     * sets the game server version
     * @param gameServerVersion the game server version to set
     */
    void setGameVersion(final @NotNull GameServerVersion gameServerVersion);

}
