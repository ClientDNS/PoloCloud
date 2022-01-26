package de.bytemc.cloud.plugin.services;

import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.AbstractSimpleServiceManager;
import de.bytemc.network.promise.ICommunicationPromise;

public class ServiceManager extends AbstractSimpleServiceManager {


    @Override
    public ICommunicationPromise<IService> startService(IService service) {
        //TODO SEND PACKET
        return null;
    }
}
