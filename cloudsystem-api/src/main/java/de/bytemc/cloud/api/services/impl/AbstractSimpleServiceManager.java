package de.bytemc.cloud.api.services.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.IServiceManager;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class AbstractSimpleServiceManager implements IServiceManager {

    private final List<IService> allCachedServices = Lists.newArrayList();

    public void registerService(IService service){
        allCachedServices.add(service);
    }
}
