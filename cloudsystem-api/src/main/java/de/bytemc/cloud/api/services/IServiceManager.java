package de.bytemc.cloud.api.services;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.network.promise.ICommunicationPromise;

import java.util.List;
import java.util.stream.Collectors;

public interface IServiceManager {

    List<IService> getAllCachedServices();

    default List<IService> getAllServicesByGroup(IServiceGroup serviceGroup) {
        return getAllCachedServices().stream().filter(it -> it.getServiceGroup().equals(serviceGroup)).collect(Collectors.toList());
    }

    default List<IService> getAllServicesByState(ServiceState serviceState){
        return getAllCachedServices().stream().filter(it -> it.getServiceState() == serviceState).collect(Collectors.toList());
    }

    default IService getServiceByNameOrNull(String name){
        return getAllCachedServices().stream().filter(it -> it.getName().equals(name)).findAny().orElse(null);
    }

    ICommunicationPromise<IService> startService(IService service);

}
