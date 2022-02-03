package de.bytemc.cloud.services.statistics;

import com.google.common.collect.Maps;
import de.bytemc.cloud.api.services.IService;

import java.util.Map;

public final class SimpleStatisticManager {

    private static final Map<IService, Long> SERVICE_START_UP = Maps.newConcurrentMap();

    public static void registerStartingProcess(IService service){
        SERVICE_START_UP.put(service, System.currentTimeMillis());
    }

    public static long getProcessingTime(IService service){
        long time = System.currentTimeMillis() - SERVICE_START_UP.getOrDefault(service, (System.currentTimeMillis() - 1));
        SERVICE_START_UP.remove(service);
        return time;
    }

}
