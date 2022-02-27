package de.polocloud.base.service.statistic;

import de.polocloud.api.service.CloudService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SimpleStatisticManager {

    private static final Map<CloudService, Long> SERVICE_START_UP = new ConcurrentHashMap<>();

    public static void registerStartingProcess(CloudService service){
        SERVICE_START_UP.put(service, System.currentTimeMillis());
    }

    public static long getProcessingTime(CloudService service){
        final var time = System.currentTimeMillis() - SERVICE_START_UP.getOrDefault(service, (System.currentTimeMillis() - 1));
        SERVICE_START_UP.remove(service);
        return time;
    }

}
