package de.bytemc.cloud;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.SimpleLoggerProvider;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.command.DefaultCommandSender;
import de.bytemc.cloud.config.NodeConfig;
import de.bytemc.cloud.database.IDatabaseManager;
import de.bytemc.cloud.database.impl.DatabaseManager;
import de.bytemc.cloud.groups.SimpleGroupManager;
import de.bytemc.cloud.node.BaseNode;
import de.bytemc.cloud.player.CloudPlayerManager;
import de.bytemc.cloud.services.ServiceManager;
import de.bytemc.cloud.services.queue.QueueService;
import de.bytemc.cloud.templates.GroupTemplateService;
import de.bytemc.network.promise.ICommunicationPromise;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Getter
public class Base extends CloudAPI {

    @Getter
    private static Base instance;
    @Getter
    private final DefaultCommandSender commandSender = new DefaultCommandSender();

    private final BaseNode node;
    private final IDatabaseManager databaseManager;
    private final IGroupManager groupManager;
    private final IServiceManager serviceManager;
    private final ICloudPlayerManager cloudPlayerManager;

    private final GroupTemplateService groupTemplateService;
    private final QueueService queueService;

    private boolean running = true;

    public Base() {
        super(CloudAPITypes.NODE);

        instance = this;
        getLoggerProvider().logMessage("§7Cloudsystem > §b@ByteMC §7| §7Developed by: §bHttpMarco §7| Date: §b19.01.2020", LogType.EMPTY);
        getLoggerProvider().logMessage(" ", LogType.EMPTY);

        this.databaseManager = new DatabaseManager();
        this.groupManager = new SimpleGroupManager();
        this.serviceManager = new ServiceManager();
        this.groupTemplateService = new GroupTemplateService();
        this.cloudPlayerManager = new CloudPlayerManager();
        this.node = new BaseNode(NodeConfig.get());

        // registered commands
        this.getCommandManager().registerCommandByPackage("de.bytemc.cloud.command.impl");

        this.queueService = new QueueService();

        // add a shutdown hook for fast closes
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));

        // print finish successfully message
        this.getLoggerProvider().logMessage("               ", LogType.EMPTY);
        this.getLoggerProvider().logMessage("§7The cloud was successfully started.", LogType.SUCCESS);
        this.getLoggerProvider().logMessage("               ", LogType.EMPTY);

        ((SimpleLoggerProvider) CloudAPI.getInstance().getLoggerProvider()).getConsoleManager().start();

        this.queueService.checkForQueue();
    }

    public void onShutdown() {
        this.running = false;
        this.getLoggerProvider().logMessage("Trying to terminate cloudsystem.");
        this.getServiceManager().getAllCachedServices()
            .forEach(service -> {
                if (((SimpleService) service).getProcess() != null)
                    ((SimpleService) service).getProcess().destroyForcibly();
            });
        try {
            FileUtils.deleteDirectory(new File("tmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ICommunicationPromise.combineAll(Lists.newArrayList(this.node.shutdownConnection(), this.databaseManager.shutdown()))
            .addCompleteListener(it -> System.exit(0))
            .addResultListener(unused -> {
                this.getLoggerProvider().logMessage("Successfully shutdown the cloudsystem.", LogType.SUCCESS);
                ((SimpleLoggerProvider) this.getLoggerProvider()).getConsoleManager().shutdownReading();
            });
    }

    public boolean isRunning() {
        return this.running;
    }

}
