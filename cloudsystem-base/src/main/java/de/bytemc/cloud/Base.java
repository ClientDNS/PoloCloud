package de.bytemc.cloud;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.command.DefaultCommandSender;
import de.bytemc.cloud.database.IDatabaseManager;
import de.bytemc.cloud.database.impl.DatabaseManager;
import de.bytemc.cloud.groups.SimpleGroupManager;
import de.bytemc.cloud.node.BaseNode;
import de.bytemc.cloud.services.ServiceManager;
import de.bytemc.cloud.services.queue.QueueService;
import de.bytemc.network.promise.CommunicationPromise;
import de.bytemc.network.promise.ICommunicationPromise;
import lombok.Getter;

@Getter
public class Base extends CloudAPI {

    @Getter private static Base instance;
    @Getter private final DefaultCommandSender commandSender = new DefaultCommandSender();

    private BaseNode node;
    private IDatabaseManager databaseManager;
    private IGroupManager groupManager;
    private IServiceManager serviceManager;

    public Base() {
        super(CloudAPITypes.NODE);

        instance = this;
        getLoggerProvider().logMessage("§7Cloudsystem » §b@ByteMC §7| §7Developed by: §bHttpMarco §7| Date: §b19.01.2020", LogType.EMPTY);
        getLoggerProvider().logMessage(" ", LogType.EMPTY);

        this.databaseManager = new DatabaseManager();
        this.groupManager = new SimpleGroupManager();
        this.serviceManager = new ServiceManager();
        this.node = new BaseNode();

        //registered commands
        getCommandManager().registerCommandByPackage("de.bytemc.cloud.command.impl");

        //print finish successfully message
        getLoggerProvider().logMessage("               ", LogType.EMPTY);
        getLoggerProvider().logMessage("§7The cloud was successfully started.", LogType.SUCCESS);
        getLoggerProvider().logMessage("               ", LogType.EMPTY);

        //add a shutdown hook for fast closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> onShutdown()));

        new QueueService();

    }

    public void onShutdown() {
        CloudAPI.getInstance().getLoggerProvider().logMessage("Trying to terminate cloudsystem.");
        ICommunicationPromise.combineAll(Lists.newArrayList(node.shutdownConnection(), databaseManager.shutdown())).addCompleteListener(it -> System.exit(0)).addResultListener(unused -> {
            CloudAPI.getInstance().getLoggerProvider().logMessage("Successfully shutdown the cloudsystem.", LogType.SUCCESS);
        });
    }

}
