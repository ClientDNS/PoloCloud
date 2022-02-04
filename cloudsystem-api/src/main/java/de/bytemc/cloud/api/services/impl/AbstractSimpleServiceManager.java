package de.bytemc.cloud.api.services.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.network.packets.player.CloudPlayerUpdatePacket;
import de.bytemc.cloud.api.network.packets.services.ServiceUpdatePacket;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.IServiceManager;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class AbstractSimpleServiceManager implements IServiceManager {

    private List<IService> allCachedServices = Lists.newArrayList();

    public AbstractSimpleServiceManager() {
        CloudAPI.getInstance().getNetworkHandler().registerPacketListener(ServiceUpdatePacket.class, (ctx, packet) -> {
            IService service = getServiceByNameOrNull(packet.getService());
            Objects.requireNonNull(service, "Updated cloud player is null.");

            service.setServiceState(packet.getState());
            service.setServiceVisibility(packet.getServiceVisibility());
            service.setMaxPlayers(packet.getMaxPlayers());

            //TODO
            System.out.println("Service update: " + packet.getService() + ";" + service.getServiceState() + ";" + service.getServiceVisibility());

        });
    }

    public void registerService(IService service) {
        allCachedServices.add(service);
    }

    public abstract void updateService(IService service);
}
