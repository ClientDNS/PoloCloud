package de.bytemc.cloud.services;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.AbstractSimpleServiceManager;
import de.bytemc.cloud.services.process.ProcessServiceStarter;
import de.bytemc.network.promise.ICommunicationPromise;

public class ServiceManager extends AbstractSimpleServiceManager {

    public void start(IService service){
        startService(service).addResultListener(it -> {
            CloudAPI.getInstance().getLoggerProvider().logMessage("The service '§b" + service.getName() + "§7' selected and will now started. (" + service.getServiceState().getName() + "§7)");
        }).addFailureListener(it -> it.printStackTrace());
    }

    public ICommunicationPromise<IService> startService(IService service){
        return new ProcessServiceStarter(service).start();
    }

}
