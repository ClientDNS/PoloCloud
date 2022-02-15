package de.bytemc.cloud.api.services.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.network.packets.IPacket;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Getter
@Setter
public final class SimpleService implements IService {

    private IServiceGroup serviceGroup;
    private int serviceID;

    private int port;
    private String hostName;
    private int maxPlayers;
    private String motd;

    private ServiceState serviceState = ServiceState.PREPARED;
    private ServiceVisibility serviceVisibility = ServiceVisibility.BLANK;

    public SimpleService(String group, int id, int port, String hostname) {
        this.serviceGroup = CloudAPI.getInstance().getGroupManager().getServiceGroupByNameOrNull(group);
        this.serviceID = id;
        this.port = port;
        this.hostName = hostname;
        assert serviceGroup != null;
        this.motd = serviceGroup.getMotd();
        this.maxPlayers = serviceGroup.getDefaultMaxPlayers();
    }

    public SimpleService(String group, int id, int port, String hostName, int maxPlayers, ServiceState serviceState, ServiceVisibility serviceVisibility, String motd) {
        this(group, id, port, hostName);
        this.maxPlayers = maxPlayers;
        this.serviceState = serviceState;
        this.serviceVisibility = serviceVisibility;
        this.motd = motd;
    }

    @Override
    public @NotNull String getName() {
        return this.serviceGroup.getName() + "-" + this.serviceID;
    }

    @Override
    public void edit(final @NotNull Consumer<IService> serviceConsumer) {
        serviceConsumer.accept(this);
        this.update();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SimpleService that = (SimpleService) o;

        if (this.serviceID != that.serviceID) return false;
        if (this.port != that.port) return false;
        return this.serviceGroup.equals(that.serviceGroup);
    }

    @Override
    public int hashCode() {
        int result = this.serviceGroup.hashCode();
        result = 31 * result + this.serviceID;
        result = 31 * result + this.port;
        return result;
    }

    public void update(){
        CloudAPI.getInstance().getServiceManager().updateService(this);
    }

    @Override
    public void sendPacket(@NotNull IPacket packet) {
        CloudAPI.getInstance().getServiceManager().sendPacketToService(this, packet);
    }
}
