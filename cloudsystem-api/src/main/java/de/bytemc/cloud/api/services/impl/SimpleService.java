package de.bytemc.cloud.api.services.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleService implements IService {

    private String group;
    private int serviceID;

    private int port;
    private String hostName;

    private Process process;

    private ServiceState serviceState = ServiceState.PREPARED;

    public SimpleService(String group, int id, int port, String hostname) {
        this.group = group;
        this.serviceID = id;
        this.port = port;
        this.hostName = hostname;
    }

    public SimpleService(String group, int id, int port, String hostName, ServiceState serviceState) {
        this(group, id, port, hostName);
        this.serviceState = serviceState;
    }

    @Override
    public String getName() {
        return this.group + "-" + this.serviceID;
    }

    @Override
    public IServiceGroup getServiceGroup() {
        return CloudAPI.getInstance().getGroupManager().getServiceGroupByNameOrNull(this.group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SimpleService that = (SimpleService) o;

        if (this.serviceID != that.serviceID) return false;
        if (this.port != that.port) return false;
        return this.group.equals(that.group);
    }

    @Override
    public int hashCode() {
        int result = this.group.hashCode();
        result = 31 * result + this.serviceID;
        result = 31 * result + this.port;
        return result;
    }

}
