package de.bytemc.cloud.api.services;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.utils.ServiceState;
import org.jetbrains.annotations.NotNull;

public interface IService {

    /**
     * @return the name of the service
     */
    @NotNull String getName();

    /**
     * @return the service id
     */
    int getServiceID();

    /**
     * @return the port of the service
     */
    int getPort();

    /**
     * @return the host name of the service
     */
    @NotNull String getHostName();

    /**
     * @return the group of the service
     */
    @NotNull IServiceGroup getServiceGroup();

    /**
     * sets the service state
     * @param serviceState the state to set
     */
    void setServiceState(final @NotNull ServiceState serviceState);

    /**
     * @return the state of the service
     */
    @NotNull ServiceState getServiceState();

    default int getOnlinePlayers() {
        return (int) CloudAPI.getInstance().getCloudPlayerManager().getAllCachedCloudPlayers().stream().filter(it -> it.getServer() != null && it.getServer().equals(this)).count();
    }

}
