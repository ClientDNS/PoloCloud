package de.bytemc.cloud.services;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.RedirectPacket;
import de.bytemc.cloud.api.network.packets.services.ServiceShutdownPacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.AbstractSimpleServiceManager;
import de.bytemc.cloud.services.process.ProcessServiceStarter;
import de.bytemc.network.packets.IPacket;
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

    public void sendPacketToService(IService service, IPacket packet) {
        Base.getInstance().getNode().getAllCachedConnectedClients().stream().filter(it -> it.getName().equals(service.getName())).findAny().ifPresent(it -> it.sendPacket(packet));
    }

    public void shutdownService(IService service){
        if(service.getServiceGroup().getNode().equals(Base.getInstance().getNode().getNodeName())){
            sendPacketToService(service, new ServiceShutdownPacket(service.getName()));
        }else{
            Base.getInstance().getNode().sendPacketToAllNodes(new RedirectPacket(service.getServiceGroup().getNode(),new ServiceShutdownPacket(service.getName())));
        }
    }


}
