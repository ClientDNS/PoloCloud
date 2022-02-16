package de.bytemc.cloud.api;

import de.bytemc.cloud.api.events.EventHandler;
import de.bytemc.cloud.api.events.IEventHandler;
import de.bytemc.cloud.api.exception.ErrorHandler;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.Logger;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.impl.NetworkHandler;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class CloudAPI {

    @Getter
    private static CloudAPI instance;

    private final CloudAPIType cloudAPITypes;
    private final INetworkHandler networkHandler;
    private final IEventHandler eventHandler;

    public CloudAPI(CloudAPIType cloudAPITypes) {
        instance = this;

        this.cloudAPITypes = cloudAPITypes;

        ErrorHandler.defaultInstance().registerDefaultThreadExceptionHandler()
            .orElse((throwable, errorHandler) -> {
                if (getLoggerProvider() == null) {
                    System.err.println("Caught an unexpected error (" + throwable.getClass().getSimpleName() + ") | (" + throwable.getMessage() + ")");
                    throwable.printStackTrace();
                    return;
                }
                getLoggerProvider().logMessage("§7Caught an §cunexpected error §7(§c" + throwable.getClass().getSimpleName() + "§7) | (§b" + throwable.getMessage() + "§7)", LogType.ERROR);
                throwable.printStackTrace();
            });

        this.networkHandler = new NetworkHandler();
        this.eventHandler = new EventHandler();
    }

    /**
     * @return the logger provider
     */
    public abstract Logger getLoggerProvider();

    /**
     * @return the group manager
     */
    public abstract @NotNull IGroupManager getGroupManager();

    /**
     * @return the service manager
     */
    public abstract @NotNull IServiceManager getServiceManager();

    /**
     * @return the player manager
     */
    public abstract @NotNull ICloudPlayerManager getCloudPlayerManager();

}

