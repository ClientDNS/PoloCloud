package de.bytemc.cloud.services.queue;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;

public class QueueService {

    private static final int MAX_BOOTABLE_SERVICES = 1;

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

    public void startService(IService service) {
        service.setServiceState(ServiceState.STARTING);

        //TODO
    }


}
