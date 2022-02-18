package de.polocloud.base.service.statistic;

import com.google.common.collect.Maps;
import de.polocloud.api.service.CloudService;

import java.util.Map;

public final class SimpleStatisticManager {

    private static final Map<CloudService, Long> SERVICE_START_UP = Maps.newConcurrentMap();

    public static void registerStartingProcess(CloudService service){
        SERVICE_START_UP.put(service, System.currentTimeMillis());
    }

    public static long getProcessingTime(CloudService service){
        long time = System.currentTimeMillis() - SERVICE_START_UP.getOrDefault(service, (System.currentTimeMillis() - 1));
        SERVICE_START_UP.remove(service);
        return time;
    }

}
