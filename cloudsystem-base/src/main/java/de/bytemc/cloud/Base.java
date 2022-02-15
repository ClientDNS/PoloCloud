package de.bytemc.cloud;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.exception.ErrorHandler;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.LoggerProvider;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.command.impl.*;
import de.bytemc.cloud.config.NodeConfig;
import de.bytemc.cloud.database.IDatabaseManager;
import de.bytemc.cloud.database.impl.DatabaseManager;
import de.bytemc.cloud.exception.DefaultExceptionCodes;
import de.bytemc.cloud.groups.SimpleGroupManager;
import de.bytemc.cloud.logger.SimpleLoggerProvider;
import de.bytemc.cloud.node.BaseNode;
import de.bytemc.cloud.player.CloudPlayerManager;
import de.bytemc.cloud.services.LocalService;
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

        new DefaultExceptionCodes();

        this.loggerProvider = new SimpleLoggerProvider();
        this.loggerProvider.logMessage("§7Cloudsystem > §b@ByteMC §7| " +
            "§7Developed by: §bHttpMarco §7| " +
            "Date: §b19.01.2020 §7| " +
            "§7Version: §b" + this.version, LogType.EMPTY);
        this.loggerProvider.logMessage(" ", LogType.EMPTY);

        // copy wrapper and plugin jar
        ErrorHandler.defaultInstance().runOnly(() -> {
            final var storageDirectory = new File("storage/jars");
            storageDirectory.mkdirs();

            Files.copy(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("wrapper.jar")),
                new File(storageDirectory, "wrapper.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("plugin.jar")),
                new File(storageDirectory, "plugin.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
            return null;
        });

        final var nodeConfig = NodeConfig.read();

        this.databaseManager = new DatabaseManager(nodeConfig.getDatabaseConfiguration());
        this.groupManager = new SimpleGroupManager();
        this.serviceManager = new ServiceManager();
        this.groupTemplateService = new GroupTemplateService();
        this.cloudPlayerManager = new CloudPlayerManager();
        this.node = new BaseNode(nodeConfig);

        // register commands
        this.getCommandManager().registerCommands(
            new ClearCommand(),
            new GroupCommand(),
            new HelpCommand(),
            new InfoCommand(),
            new ServiceCommand(),
            new ShutdownCommand());

        this.queueService = new QueueService();

        // add a shutdown hook for fast closes
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));

        // print finish successfully message
        this.loggerProvider.logMessage("               ", LogType.EMPTY);
        this.loggerProvider.logMessage("§7The cloud was successfully started.", LogType.SUCCESS);
        this.loggerProvider.logMessage("               ", LogType.EMPTY);

        ((SimpleLoggerProvider) this.loggerProvider).getConsoleManager().start();

        this.queueService.checkForQueue();
    }

    public void onShutdown() {
        if (!this.running) return;
        this.running = false;
        this.loggerProvider.logMessage("Trying to terminate cloudsystem.");
        this.serviceManager.getAllCachedServices()
            .forEach(service -> {
                if (service instanceof LocalService localService) localService.stop();
            });

        // delete wrapper and plugin jars
        ErrorHandler.defaultInstance().runOnly(() -> {
            final var storageDirectory = new File("storage/jars");

            Files.deleteIfExists(new File(storageDirectory, "wrapper.jar").toPath());
            Files.deleteIfExists(new File(storageDirectory, "plugin.jar").toPath());
            return null;
        });

        ICommunicationPromise.combineAll(Lists.newArrayList(this.node.shutdownConnection(), this.databaseManager.shutdown()))
            .addCompleteListener(voidICommunicationPromise -> ErrorHandler.defaultInstance().runOnly(() -> {
                FileUtils.deleteDirectory(new File("tmp"));
                return null;
            }))
            .addResultListener(unused -> {
                this.loggerProvider.logMessage("Successfully shutdown the cloudsystem.", LogType.SUCCESS);
                ((SimpleLoggerProvider) this.loggerProvider).getConsoleManager().shutdownReading();
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
