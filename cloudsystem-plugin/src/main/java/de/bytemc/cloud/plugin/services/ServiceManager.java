package de.bytemc.cloud.plugin.services;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.services.ServiceShutdownPacket;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.AbstractSimpleServiceManager;
import de.bytemc.cloud.plugin.CloudPlugin;
import de.bytemc.network.promise.ICommunicationPromise;

public class ServiceManager extends AbstractSimpleServiceManager {

    public ServiceManager(){
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceShutdownPacket.class, (ctx, packet) -> {
            CloudPlugin.getInstance().getPlugin().shutdown();
        });
    }

    @Override
    public ICommunicationPromise<IService> startService(IService service) {
        //TODO SEND PACKET
        return null;
    }
}
