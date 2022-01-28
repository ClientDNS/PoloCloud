package de.bytemc.cloud.services.queue;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.services.ServiceManager;
import de.bytemc.cloud.services.ports.PortHandler;

public class QueueService {

    private static final int MAX_BOOTABLE_SERVICES = 1;

    public QueueService() {
        CloudAPI.getInstance().getGroupManager().getAllCachedServiceGroups()
            .stream()
            .filter(it -> getAmountOfGroupServices(it) <= it.getMinOnlineService())
            .filter(it -> it.getNode().equalsIgnoreCase(Base.getInstance().getNode().getNodeName()))
            .forEach(it -> {
                IService service = new SimpleService(it.getGroup(), getPossibleServiceIDByGroup(it), PortHandler.getNextPort(it));
                CloudAPI.getInstance().getServiceManager().getAllCachedServices().add(service);
                CloudAPI.getInstance().getLoggerProvider().logMessage("The group '§b" + it.getGroup() + "§7' start new instance of '§b" + service.getName() + "§7' (" + service.getServiceState().getName() + "§7)");
            });
    }

    public void start() {
        if(minBootableServiceExists()) return;

        //TODO
        for (IService allCachedService : CloudAPI.getInstance().getServiceManager().getAllCachedServices()) {
            ((ServiceManager) CloudAPI.getInstance().getServiceManager()).start(allCachedService);
        }
    }



    private boolean minBootableServiceExists(){
        return getAmountOfBootableServices() >= MAX_BOOTABLE_SERVICES;
    }

    private int getAmountOfBootableServices(){
        return CloudAPI.getInstance().getServiceManager().getAllServicesByState(ServiceState.STARTING).size();
    }

    public int getAmountOfGroupServices(IServiceGroup serviceGroup) {
        return (int) CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream().filter(it -> it.getServiceGroup().equals(serviceGroup)).count();
    }

    private int getPossibleServiceIDByGroup(final IServiceGroup serviceGroup){
        int id = 1;
        while (isServiceIDAlreadyExists(serviceGroup, id)) id++;
        return id;
    }

    private boolean isServiceIDAlreadyExists(final IServiceGroup serviceGroup, int id) {
        return CloudAPI.getInstance().getServiceManager().getAllServicesByGroup(serviceGroup).stream().anyMatch(it -> id == it.getServiceID());
    }
}
