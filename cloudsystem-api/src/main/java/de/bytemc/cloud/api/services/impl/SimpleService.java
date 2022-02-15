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

    private IServiceGroup group;
    private int serviceId;

    private int port;
    private String hostName;
    private int maxPlayers;
    private String motd;

    private ServiceState serviceState = ServiceState.PREPARED;
    private ServiceVisibility serviceVisibility = ServiceVisibility.BLANK;

    public SimpleService(String group, int id, int port, String hostname) {
        this.group = CloudAPI.getInstance().getGroupManager().getServiceGroupByNameOrNull(group);
        this.serviceId = id;
        this.port = port;
        this.hostName = hostname;
        assert this.group != null;
        this.motd = this.group.getMotd();
        this.maxPlayers = this.group.getDefaultMaxPlayers();
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
        return this.group.getName() + "-" + this.serviceId;
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

        if (this.serviceId != that.serviceId) return false;
        if (this.port != that.port) return false;
        return this.group.equals(that.group);
    }

    @Override
    public int hashCode() {
        int result = this.group.hashCode();
        result = 31 * result + this.serviceId;
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

    @Override
    public void executeCommand(@NotNull String command) {
        // TODO send packet to node that executes the command
    }

    @Override
    public void stop() {
        // TODO send packet to node that to stop the service
    }

}
