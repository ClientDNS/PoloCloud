package de.bytemc.cloud.services.statistics;

import com.google.common.collect.Maps;
import de.bytemc.cloud.api.services.IService;

import java.util.Map;

public class SimpleStatisticManager {

    private static final Map<IService, Long> serviceStartUp = Maps.newConcurrentMap();

    public static void registerStartingProcess(IService service){
        serviceStartUp.put(service, System.currentTimeMillis());
    }

    public static long getProcessingTime(IService service){
        long time = System.currentTimeMillis() - serviceStartUp.getOrDefault(service, (System.currentTimeMillis() - 1));
        serviceStartUp.remove(service);
        return time;
    }

}
