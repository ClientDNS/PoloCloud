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

    private Process process;

    private ServiceState serviceState = ServiceState.PREPARED;

    public SimpleService(String group, int id, int port) {
        this.group = group;
        this.serviceID = id;
        this.port = port;
    }

    @Override
    public String getName() {
        return group + "-" + serviceID;
    }

    @Override
    public IServiceGroup getServiceGroup() {
        return CloudAPI.getInstance().getGroupManager().getServiceGroupByNameOrNull(group);
    }

    public void shutdown() {
       if(process != null && process.isAlive()) process.destroy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleService that = (SimpleService) o;

        if (serviceID != that.serviceID) return false;
        if (port != that.port) return false;
        return group.equals(that.group);
    }

    @Override
    public int hashCode() {
        int result = group.hashCode();
        result = 31 * result + serviceID;
        result = 31 * result + port;
        return result;
    }
}
