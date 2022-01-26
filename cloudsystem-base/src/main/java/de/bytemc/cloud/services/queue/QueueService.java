package de.bytemc.cloud.services.queue;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;

public class QueueService {

    private static final int MAX_BOOTABLE_SERVICES = 1;

    public QueueService() {
        CloudAPI.getInstance().getGroupManager().getAllCachedServiceGroups().stream().filter(it -> getAmountOfGroupServices(it) <= it.getMinOnlineService()).forEach(it -> {
            //TODO PACKET & START
            IService service = it.newService(1);
            CloudAPI.getInstance().getLoggerProvider().logMessage("The group '§b" + it.getGroup() + "§7' start new instance of '§b" + service.getName() + "§7' (" + service.getServiceState().getName() + "§7)");
        });
        check();
    }

    public void check() {
        if(minBootableServiceExists()) return;
        int bootable = MAX_BOOTABLE_SERVICES - getAmountOfBootableServices();
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

    public void startService(IService service) {
        service.setServiceState(ServiceState.STARTING);

        //TODO
    }


}
