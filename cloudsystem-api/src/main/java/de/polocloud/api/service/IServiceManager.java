package de.polocloud.api.service;

import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.network.packet.Packet;
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
     * @param services the services to set
     */
    void setAllCachedServices(@NotNull List<IService> services);

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
        return this.getAllCachedServices().stream().filter(it -> it.getName().equalsIgnoreCase(name)).findFirst();
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
    void startService(@NotNull IService service);

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
    void sendPacketToService(IService service, Packet packet);

}
