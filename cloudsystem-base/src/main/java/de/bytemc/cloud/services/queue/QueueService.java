package de.bytemc.cloud.services.queue;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.services.ServiceManager;

public class QueueService {

    private static final int MAX_BOOTABLE_SERVICES = 1;

    public QueueService() {
        CloudAPI.getInstance().getGroupManager().getAllCachedServiceGroups().stream().filter(it -> getAmountOfGroupServices(it) <= it.getMinOnlineService()).forEach(it -> {
            IService service = it.newService(getPossibleServiceIDByGroup(it));
            CloudAPI.getInstance().getServiceManager().getAllCachedServices().add(service);
            CloudAPI.getInstance().getLoggerProvider().logMessage("The group '§b" + it.getGroup() + "§7' start new instance of '§b" + service.getName() + "§7' (" + service.getServiceState().getName() + "§7)");
            //TODO change
            ((ServiceManager)CloudAPI.getInstance().getServiceManager()).start(service);
        });
        check();
    }

    public void check() {
        if(minBootableServiceExists()) return;



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
