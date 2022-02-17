package de.polocloud.base.service.queue;

import de.polocloud.base.Base;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.network.packet.service.ServiceAddPacket;
import de.polocloud.api.service.IService;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.base.service.LocalService;
import de.polocloud.base.service.ServiceManager;
import de.polocloud.base.service.port.PortHandler;

import java.util.List;

public final class QueueService {

    private static final int MAX_BOOTABLE_SERVICES = 1;

    public QueueService() {
        this.addServiceToQueueWhereProvided();
    }

    public void checkForQueue() {
        if (!Base.getInstance().isRunning()) return;
        this.addServiceToQueueWhereProvided();
        if (this.minBootableServiceExists()) return;

        final List<IService> services = CloudAPI.getInstance().getServiceManager().getAllServicesByState(ServiceState.PREPARED)
            .stream().filter(service -> service.getGroup().getNode().equalsIgnoreCase(Base.getInstance().getNode().getName())).toList();
        if (services.isEmpty()) return;
        ((ServiceManager) CloudAPI.getInstance().getServiceManager()).start(services.get(0));
    }

    public void addServiceToQueueWhereProvided() {
        Base base = Base.getInstance();
        CloudAPI.getInstance().getGroupManager().getAllCachedServiceGroups().stream()
            .filter(serviceGroup -> serviceGroup.getNode().equalsIgnoreCase(base.getNode().getName()))
            .filter(serviceGroup -> this.getAmountOfGroupServices(serviceGroup) < serviceGroup.getMinOnlineService())
            .forEach(serviceGroup -> {
                final var service = new LocalService(serviceGroup, this.getPossibleServiceIDByGroup(serviceGroup),
                    PortHandler.getNextPort(serviceGroup), base.getNode().getHostName());
                CloudAPI.getInstance().getServiceManager().getAllCachedServices().add(service);
                base.getNode().sendPacketToAll(new ServiceAddPacket(service));
                CloudAPI.getInstance().getLogger()
                    .log("The group '§b" + serviceGroup.getName() + "§7' start new instance of '§b" + service.getName()
                        + "§7' (" + service.getServiceState().getName() + "§7)");
            });
    }

    private boolean minBootableServiceExists() {
        return this.getAmountOfBootableServices() >= MAX_BOOTABLE_SERVICES;
    }

    private int getAmountOfBootableServices() {
        return CloudAPI.getInstance().getServiceManager().getAllServicesByState(ServiceState.STARTING).size();
    }

    public int getAmountOfGroupServices(final IServiceGroup serviceGroup) {
        return (int) CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream()
            .filter(service -> service.getGroup().equals(serviceGroup)).count();
    }

    private int getPossibleServiceIDByGroup(final IServiceGroup serviceGroup) {
        int id = 1;
        while (this.isServiceIDAlreadyExists(serviceGroup, id)) id++;
        return id;
    }

    private boolean isServiceIDAlreadyExists(final IServiceGroup serviceGroup, int id) {
        return CloudAPI.getInstance().getServiceManager().getAllServicesByGroup(serviceGroup).stream().anyMatch(it -> id == it.getServiceId());
    }

}
