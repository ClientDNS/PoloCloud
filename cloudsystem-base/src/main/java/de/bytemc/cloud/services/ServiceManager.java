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

    public void start(final IService service) {
        this.startService(service).addResultListener(it ->
            CloudAPI.getInstance().getLoggerProvider()
                .logMessage("The service '§b" + service.getName() + "§7' selected and will now started.")).addFailureListener(Throwable::printStackTrace);
    }

    public ICommunicationPromise<IService> startService(final IService service) {
        return new ProcessServiceStarter(service).start();
    }

    public void sendPacketToService(final IService service, final IPacket packet) {
        Base.getInstance().getNode().getAllCachedConnectedClients().stream().filter(it -> it.getName().equals(service.getName())).findAny().ifPresent(it -> it.sendPacket(packet));
    }

    public void shutdownService(final IService service) {
        if (service.getServiceGroup().getNode().equals(Base.getInstance().getNode().getNodeName())) {
            this.sendPacketToService(service, new ServiceShutdownPacket(service.getName()));
        } else {
            Base.getInstance().getNode().sendPacketToAllNodes(new RedirectPacket(service.getServiceGroup().getNode(), new ServiceShutdownPacket(service.getName())));
        }
    }


}
