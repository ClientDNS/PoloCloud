package de.bytemc.cloud.api.fallback;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FallbackHandler {

    public static Optional<IService> getLobbyFallbackOrNull() {
        return getAllPossibleOnlineFallbackServices()
            .stream()
            .min(Comparator.comparing(IService::getOnlinePlayers));
    }

    public static boolean isFallbackAvailable(){
        return !getLobbyFallbackOrNull().isEmpty();
    }

    private static List<IService> getAllPossibleOnlineFallbackServices() {
        return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream()
            .filter(it -> it.getServiceState() == ServiceState.ONLINE)
            .filter(it -> it.getServiceVisibility() == ServiceVisibility.VISIBLE)
            .filter(it -> !it.getServiceGroup().getGameServerVersion().isProxy())
            .filter(it -> it.getServiceGroup().isFallbackGroup()).collect(Collectors.toList());
    }

}
