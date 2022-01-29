package de.bytemc.cloud;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.common.GsonFactory;
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

    private BaseNode node;
    private IDatabaseManager databaseManager;
    private IGroupManager groupManager;
    private IServiceManager serviceManager;
    private ICloudPlayerManager cloudPlayerManager;

    private GroupTemplateService groupTemplateService;
    private QueueService queueService;

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
        this.node = new BaseNode(NodeConfig.read());

        //registered commands
        getCommandManager().registerCommandByPackage("de.bytemc.cloud.command.impl");

        queueService = new QueueService();

        //add a shutdown hook for fast closes
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));

        //print finish successfully message
        getLoggerProvider().logMessage("               ", LogType.EMPTY);
        getLoggerProvider().logMessage("§7The cloud was successfully started.", LogType.SUCCESS);
        getLoggerProvider().logMessage("               ", LogType.EMPTY);

        ((SimpleLoggerProvider) CloudAPI.getInstance().getLoggerProvider()).getConsoleManager().start();

        queueService.checkForQueue();
    }

    public void onShutdown() {
        this.running = false;
        CloudAPI.getInstance().getLoggerProvider().logMessage("Trying to terminate cloudsystem.");
        CloudAPI.getInstance().getServiceManager().getAllCachedServices()
            .forEach(service -> {
                if (((SimpleService) service).getProcess() != null) ((SimpleService) service).getProcess().destroyForcibly();
            });
        try {
            FileUtils.deleteDirectory(new File("tmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ICommunicationPromise.combineAll(Lists.newArrayList(node.shutdownConnection(), databaseManager.shutdown()))
            .addCompleteListener(it -> System.exit(0))
            .addResultListener(unused -> {
                CloudAPI.getInstance().getLoggerProvider().logMessage("Successfully shutdown the cloudsystem.", LogType.SUCCESS);
                ((SimpleLoggerProvider) CloudAPI.getInstance().getLoggerProvider()).getConsoleManager().shutdownReading();
            });
    }

    public boolean isRunning() {
        return this.running;
    }

}
