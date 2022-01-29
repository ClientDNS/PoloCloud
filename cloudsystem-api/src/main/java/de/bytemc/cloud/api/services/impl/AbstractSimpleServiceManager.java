package de.bytemc.cloud.api.services.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.IServiceManager;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class AbstractSimpleServiceManager implements IServiceManager {

    private List<IService> allCachedServices = Lists.newArrayList();

    public void registerService(IService service) {
        allCachedServices.add(service);
    }

}
