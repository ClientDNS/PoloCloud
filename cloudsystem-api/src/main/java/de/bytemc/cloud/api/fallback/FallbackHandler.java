package de.bytemc.cloud.api.fallback;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.services.IService;

import java.util.Comparator;
import java.util.Optional;

public class FallbackHandler {

    public static Optional<IService> getLobbyFallbackOrNull() {
        return CloudAPI.getInstance().getServiceManager().getAllPossibleOnlineFallbackServices()
            .stream()
            .min(Comparator.comparing(IService::getOnlinePlayers));
    }

}
