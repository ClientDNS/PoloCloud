package de.bytemc.cloud.api.services.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SimpleService implements IService {

    private IServiceGroup serviceGroup;
    private int serviceID;

    private int port;
    private String hostName;
    private int maxPlayers;

    private Process process;

    private ServiceState serviceState = ServiceState.PREPARED;

    public SimpleService(String group, int id, int port, String hostname) {
        this.serviceGroup = CloudAPI.getInstance().getGroupManager().getServiceGroupByNameOrNull(group);
        this.serviceID = id;
        this.port = port;
        this.hostName = hostname;
        this.maxPlayers = serviceGroup.getDefaultMaxPlayers();
    }

    public SimpleService(String group, int id, int port, String hostName, int maxPlayers, ServiceState serviceState) {
        this(group, id, port, hostName);
        this.maxPlayers = maxPlayers;
        this.serviceState = serviceState;
    }

    @Override
    public @NotNull String getName() {
        return this.serviceGroup.getName() + "-" + this.serviceID;
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

}
