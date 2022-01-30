package de.bytemc.cloud.api.services;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.utils.ServiceState;

public interface IService {

    String getName();

    int getServiceID();

    int getPort();

    String getHostName();

    IServiceGroup getServiceGroup();

    void setServiceState(ServiceState serviceState);

    ServiceState getServiceState();

    default int getOnlinePlayers() {
        return (int) CloudAPI.getInstance().getCloudPlayerManager().getAllCachedCloudPlayers().stream().filter(it -> it.getServer().equals(this)).count();
    }

}
