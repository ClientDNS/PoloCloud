package de.bytemc.cloud.api.services;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.network.promise.ICommunicationPromise;

import java.util.List;
import java.util.stream.Collectors;

public interface IServiceManager {

    List<IService> getAllCachedServices();

    default List<IService> getAllServicesByGroup(IServiceGroup serviceGroup) {
        return this.getAllCachedServices().stream().filter(it -> it.getServiceGroup().equals(serviceGroup)).collect(Collectors.toList());
    }

    default List<IService> getAllServicesByState(ServiceState serviceState) {
        return this.getAllCachedServices().stream().filter(it -> it.getServiceState() == serviceState).collect(Collectors.toList());
    }

    default IService getServiceByNameOrNull(String name) {
        return this.getAllCachedServices().stream().filter(it -> it.getName().equals(name)).findAny().orElse(null);
    }

    default List<IService> getAllPossibleOnlineFallbackServices() {
        return this.getAllCachedServices().stream()
            .filter(it -> it.getServiceState() == ServiceState.ONLINE)
            .filter(it -> !it.getServiceGroup().getGameServerVersion().isProxy())
            .filter(it -> it.getServiceGroup().isFallbackGroup()).collect(Collectors.toList());
    }

    ICommunicationPromise<IService> startService(IService service);

}
