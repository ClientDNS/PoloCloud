package de.bytemc.cloud.services.queue;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.network.packets.services.ServiceAddPacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.services.ServiceManager;
import de.bytemc.cloud.services.ports.PortHandler;

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
            .stream().filter(it -> it.getServiceGroup().getNode().equalsIgnoreCase(Base.getInstance().getNode().getNodeName())).toList();
        if (services.isEmpty()) return;
        ((ServiceManager) CloudAPI.getInstance().getServiceManager()).start(services.get(0));
    }

    public void addServiceToQueueWhereProvided() {
        Base base = Base.getInstance();
        CloudAPI.getInstance().getGroupManager().getAllCachedServiceGroups().stream()
            .filter(serviceGroup -> serviceGroup.getNode().equalsIgnoreCase(base.getNode().getNodeName()))
            .filter(serviceGroup -> this.getAmountOfGroupServices(serviceGroup) < serviceGroup.getMinOnlineService())
            .forEach(serviceGroup -> {
                final var service = new SimpleService(serviceGroup.getName(), this.getPossibleServiceIDByGroup(serviceGroup), PortHandler.getNextPort(serviceGroup), base.getNode().getHostName());
                CloudAPI.getInstance().getServiceManager().getAllCachedServices().add(service);
                base.getNode().sendPacketToAll(new ServiceAddPacket(service));
                CloudAPI.getInstance().getLoggerProvider()
                    .logMessage("The group '§b" + serviceGroup.getName() + "§7' start new instance of '§b" + service.getName() + "§7' (" + service.getServiceState().getName() + "§7)");
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
            .filter(it -> it.getServiceGroup().equals(serviceGroup)).count();
    }

    private int getPossibleServiceIDByGroup(final IServiceGroup serviceGroup) {
        int id = 1;
        while (this.isServiceIDAlreadyExists(serviceGroup, id)) id++;
        return id;
    }

    private boolean isServiceIDAlreadyExists(final IServiceGroup serviceGroup, int id) {
        return CloudAPI.getInstance().getServiceManager().getAllServicesByGroup(serviceGroup).stream().anyMatch(it -> id == it.getServiceID());
    }

}
