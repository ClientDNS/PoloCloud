package de.bytemc.cloud.api.services;

import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.promise.ICommunicationPromise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface IServiceManager {

    /**
     * @return all cached service
     */
    @NotNull List<IService> getAllCachedServices();

    /**
     * gets all services by a group
     * @param serviceGroup the group of the services
     * @return the services of a group
     */
    default List<IService> getAllServicesByGroup(@NotNull IServiceGroup serviceGroup) {
        return this.getAllCachedServices().stream().filter(service -> service.getGroup().equals(serviceGroup)).collect(Collectors.toList());
    }

    /**
     * gets all services of a state
     * @param serviceState the state of the services
     * @return the services of a state
     */
    default List<IService> getAllServicesByState(@NotNull ServiceState serviceState) {
        return this.getAllCachedServices().stream().filter(it -> it.getServiceState() == serviceState).collect(Collectors.toList());
    }

    /**
     * gets a service
     * @param name the name of the service
     * @return the service or null when the service does not exist
     */
    default @NotNull Optional<IService> getService(@NotNull String name) {
        return this.getAllCachedServices().stream().filter(it -> it.getName().equals(name)).findFirst();
    }

    /**
     * gets a service
     * @param name the name of the service
     * @return the service or null when the service does not exist
     */
    default @Nullable IService getServiceByNameOrNull(@NotNull String name) {
        return this.getService(name).orElse(null);
    }

    /**
     * starts a service
     * @param service the service to start
     */
    ICommunicationPromise<IService> startService(@NotNull IService service);

    /**
     * update a service
     * @param service the service to start
     */
    void updateService(IService service);

    /**
     * send a service a packet
     * @param service the service to start
     * @param packet the packet to send
     */
    void sendPacketToService(IService service, IPacket packet);

}
