package de.polocloud.api.service;

import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.network.packet.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ServiceManager {

    /**
     * @return all cached service
     */
    @NotNull List<CloudService> getAllCachedServices();

    /**
     * @param services the services to set
     */
    void setAllCachedServices(@NotNull List<CloudService> services);

    /**
     * gets all services by a group
     * @param serviceGroup the group of the services
     * @return the services of a group
     */
    default List<CloudService> getAllServicesByGroup(@NotNull ServiceGroup serviceGroup) {
        return this.getAllCachedServices().stream().filter(service -> service.getGroup().equals(serviceGroup)).collect(Collectors.toList());
    }

    /**
     * gets all services of a state
     * @param serviceState the state of the services
     * @return the services of a state
     */
    default List<CloudService> getAllServicesByState(@NotNull ServiceState serviceState) {
        return this.getAllCachedServices().stream().filter(it -> it.getServiceState() == serviceState).collect(Collectors.toList());
    }

    /**
     * gets a service
     * @param name the name of the service
     * @return the service or null when the service does not exist
     */
    default @NotNull Optional<CloudService> getService(@NotNull String name) {
        return this.getAllCachedServices().stream().filter(it -> it.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * gets a service
     * @param name the name of the service
     * @return the service or null when the service does not exist
     */
    default @Nullable CloudService getServiceByNameOrNull(@NotNull String name) {
        return this.getService(name).orElse(null);
    }

    /**
     * starts a service
     * @param service the service to start
     */
    void startService(@NotNull CloudService service);

    /**
     * update a service
     * @param service the service to start
     */
    void updateService(CloudService service);

    /**
     * send a service a packet
     * @param service the service to start
     * @param packet the packet to send
     */
    void sendPacketToService(CloudService service, Packet packet);

}
