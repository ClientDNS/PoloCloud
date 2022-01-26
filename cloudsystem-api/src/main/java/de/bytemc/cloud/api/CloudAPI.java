package de.bytemc.cloud.api;

import de.bytemc.cloud.api.command.CommandManager;
import de.bytemc.cloud.api.command.SimpleCommandManager;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import de.bytemc.cloud.api.logger.LoggerProvider;
import de.bytemc.cloud.api.logger.SimpleLoggerProvider;
import de.bytemc.cloud.api.logger.exception.ExceptionHandler;
import de.bytemc.cloud.api.network.INetworkHandler;
import de.bytemc.cloud.api.network.NetworkHandler;
import lombok.Getter;

@Getter
public abstract class CloudAPI implements ICloudAPI {

    @Getter
    private static CloudAPI instance;

    private final CloudAPITypes cloudAPITypes;
    private final LoggerProvider loggerProvider;
    private final CommandManager commandManager;
    private final INetworkHandler networkHandler;

    public CloudAPI(CloudAPITypes cloudAPITypes) {
        instance = this;

        new ExceptionHandler();

        this.cloudAPITypes = cloudAPITypes;
        this.loggerProvider = new SimpleLoggerProvider();
        this.networkHandler = new NetworkHandler();
        this.commandManager = new SimpleCommandManager();

    }

    public abstract ICommandSender getCommandSender();

}

