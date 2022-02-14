package de.bytemc.cloud.api;

import de.bytemc.cloud.api.command.CommandManager;
import de.bytemc.cloud.api.command.SimpleCommandManager;
import de.bytemc.cloud.api.events.EventHandler;
import de.bytemc.cloud.api.events.IEventHandler;
import de.bytemc.cloud.api.exception.ErrorHandler;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.LoggerProvider;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.impl.NetworkHandler;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

@Getter
public abstract class CloudAPI {

    @Getter
    private static CloudAPI instance;

    private final CloudAPITypes cloudAPITypes;
    private final CommandManager commandManager;
    private final INetworkHandler networkHandler;
    private final IEventHandler eventHandler;

    public CloudAPI(CloudAPITypes cloudAPITypes) {
        instance = this;

        this.cloudAPITypes = cloudAPITypes;

        ErrorHandler.defaultInstance().registerDefaultThreadExceptionHandler()
            .onError(SQLException.class, ((throwable, errorHandler) -> {
                if (getLoggerProvider() == null) {
                    System.err.println("SQLError occurred, check your database credentials! (" + throwable.getMessage() + ")");
                    return;
                }
                getLoggerProvider().logMessage("§cSQLError occurred§7, check your §bdatabase credentials! §7(§b" + throwable.getMessage() + "§7)", LogType.ERROR);
            }))
            .orElse((throwable, errorHandler) -> {
                if (getLoggerProvider() == null) {
                    System.err.println("Caught an unexpected error (" + throwable.getMessage() + ")");
                    return;
                }
                getLoggerProvider().logMessage("§7Caught an §cunexpected error §7(§b" + throwable.getMessage() + "§7)", LogType.ERROR);
            });

        this.networkHandler = new NetworkHandler();
        this.commandManager = new SimpleCommandManager();
        this.eventHandler = new EventHandler();
    }

    /**
     * @return the logger provider
     */
    public abstract LoggerProvider getLoggerProvider();

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

