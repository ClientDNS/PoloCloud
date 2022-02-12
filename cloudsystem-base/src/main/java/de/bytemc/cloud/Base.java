package de.bytemc.cloud;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.LoggerProvider;
import de.bytemc.cloud.logger.SimpleLoggerProvider;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.command.DefaultCommandSender;
import de.bytemc.cloud.config.NodeConfig;
import de.bytemc.cloud.database.IDatabaseManager;
import de.bytemc.cloud.database.impl.DatabaseManager;
import de.bytemc.cloud.groups.SimpleGroupManager;
import de.bytemc.cloud.logger.exception.ExceptionHandler;
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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.jar.Manifest;

@Getter
public class Base extends CloudAPI {

    @Getter
    private static Base instance;
    @Getter
    private final DefaultCommandSender commandSender = new DefaultCommandSender();

    private String version;

    private final LoggerProvider loggerProvider;
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

        try (final var stream = this.getClass().getClassLoader().getResources("META-INF/MANIFEST.MF")
            .nextElement().openStream()) {
            this.version = new Manifest(stream).getMainAttributes().getValue("Version");
        } catch (IOException e) {
            e.printStackTrace();
        }

        new ExceptionHandler();

        this.loggerProvider = new SimpleLoggerProvider();
        this.loggerProvider.logMessage("§7Cloudsystem > §b@ByteMC §7| " +
            "§7Developed by: §bHttpMarco §7| " +
            "Date: §b19.01.2020 §7| " +
            "§bVersion: " + this.version, LogType.EMPTY);
        this.loggerProvider.logMessage(" ", LogType.EMPTY);

        // copy wrapper and plugin jar
        try {
            final File storageDirectory = new File("storage/jars");

            Files.copy(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("wrapper.jar")),
                new File(storageDirectory, "wrapper.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("plugin.jar")),
                new File(storageDirectory, "plugin.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        if (!this.running) return;
        this.running = false;
        this.getLoggerProvider().logMessage("Trying to terminate cloudsystem.");
        this.getServiceManager().getAllCachedServices()
            .forEach(service -> {
                if (((SimpleService) service).getProcess() != null)
                    ((SimpleService) service).getProcess().destroyForcibly();
            });

        // delete wrapper and plugin jars
        try {
            final File storageDirectory = new File("storage/jars");

            Files.deleteIfExists(new File(storageDirectory, "wrapper.jar").toPath());
            Files.deleteIfExists(new File(storageDirectory, "plugin.jar").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ICommunicationPromise.combineAll(Lists.newArrayList(this.node.shutdownConnection(), this.databaseManager.shutdown()))
            .addCompleteListener(voidICommunicationPromise -> {
                try {
                    FileUtils.deleteDirectory(new File("tmp"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })
            .addResultListener(unused -> {
                this.getLoggerProvider().logMessage("Successfully shutdown the cloudsystem.", LogType.SUCCESS);
                ((SimpleLoggerProvider) this.getLoggerProvider()).getConsoleManager().shutdownReading();
                System.exit(0);
            });
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public LoggerProvider getLoggerProvider() {
        return this.loggerProvider;
    }

}
