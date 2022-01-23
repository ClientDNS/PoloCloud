package de.bytemc.cloud.api.services;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.utils.ServiceState;

public interface IService {

    String getName();

    int getServiceID();

    int getPort();

    IServiceGroup getServiceGroup();

    ServiceState getServiceState();

}
