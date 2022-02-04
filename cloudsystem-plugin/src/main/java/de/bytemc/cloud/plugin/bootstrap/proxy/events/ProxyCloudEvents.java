package de.bytemc.cloud.plugin.bootstrap.proxy.events;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.IEventHandler;
import de.bytemc.cloud.api.events.events.CloudServiceRegisterEvent;
import de.bytemc.cloud.api.events.events.CloudServiceRemoveEvent;
import de.bytemc.cloud.api.events.events.CloudServiceUpdateEvent;
import de.bytemc.cloud.api.services.IService;
import net.md_5.bungee.api.ProxyServer;

import java.net.InetSocketAddress;

public final class ProxyCloudEvents {

    public ProxyCloudEvents() {

        //register default fallback
        registerFallbackService();

        //load all current groups
        for (IService allCachedService : CloudAPI.getInstance().getServiceManager().getAllCachedServices()) {
            if (!allCachedService.getServiceGroup().getGameServerVersion().isProxy()) registerService(allCachedService);
        }

        //register events
        IEventHandler eventHandler = CloudAPI.getInstance().getEventHandler();
        eventHandler.registerEvent(CloudServiceRegisterEvent.class, event -> {
            if(!event.getService().getServiceGroup().getGameServerVersion().isProxy()) registerService(event.getService());
        });

        eventHandler.registerEvent(CloudServiceRemoveEvent.class, event -> unregisterService(event.getService()));
    }

    private void registerService(String name, InetSocketAddress socketAddress) {
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, socketAddress, "Service", false));
    }

    public void unregisterService(String name) {
        ProxyServer.getInstance().getServers().remove(name);
    }

    public void registerService(IService service) {
        registerService(service.getName(), new InetSocketAddress(service.getHostName(), service.getPort()));
    }

    private void registerFallbackService() {
        registerService("fallback", new InetSocketAddress("127.0.0.1", 0));
    }

}
