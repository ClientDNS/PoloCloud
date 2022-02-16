package de.polocloud.base;

import com.google.common.collect.Lists;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.CloudAPIType;
import de.polocloud.base.exception.ErrorHandler;
import de.polocloud.api.groups.IGroupManager;
import de.polocloud.api.json.Document;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.logger.Logger;
import de.polocloud.api.player.IPlayerManager;
import de.polocloud.api.service.IServiceManager;
import de.polocloud.base.command.CommandManager;
import de.polocloud.base.command.SimpleCommandManager;
import de.polocloud.base.command.defaults.*;
import de.polocloud.base.config.CloudConfiguration;
import de.polocloud.base.database.IDatabaseManager;
import de.polocloud.base.database.impl.DatabaseManager;
import de.polocloud.base.exception.DefaultExceptionCodes;
import de.polocloud.base.group.SimpleGroupManager;
import de.polocloud.base.logger.SimpleLogger;
import de.polocloud.base.node.BaseNode;
import de.polocloud.base.player.SimplePlayerManager;
import de.polocloud.base.service.LocalService;
import de.polocloud.base.service.ServiceManager;
import de.polocloud.base.service.queue.QueueService;
import de.polocloud.base.templates.GroupTemplateService;
import de.polocloud.network.promise.ICommunicationPromise;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.jar.Manifest;

@Getter
public final class Base extends CloudAPI {

    @Getter
    private static Base instance;

    private String version;

    private CloudConfiguration config;

    private final CommandManager commandManager;
    private final BaseNode node;
    private final IDatabaseManager databaseManager;
    private final IGroupManager groupManager;
    private final IServiceManager serviceManager;
    private final IPlayerManager playerManager;
    private final GroupTemplateService groupTemplateService;
    private final QueueService queueService;
    private boolean running = true;

    public Base() {
        super(CloudAPIType.NODE);

        instance = this;

        try (final var stream = this.getClass().getClassLoader().getResources("META-INF/MANIFEST.MF")
            .nextElement().openStream()) {
            this.version = new Manifest(stream).getMainAttributes().getValue("Version");
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.logger = new SimpleLogger();

        ErrorHandler.defaultInstance().registerDefaultThreadExceptionHandler()
            .orElse((throwable, errorHandler) -> {
                this.getLogger().log("§7Caught an §cunexpected error §7(§c" + throwable.getClass().getSimpleName() + "§7) " +
                    "| (§b" + throwable.getMessage() + "§7)", LogType.ERROR);
                throwable.printStackTrace();
            });

        this.loadConfig(new File("config.json"));

        new DefaultExceptionCodes();

        this.logger.log("§7Cloudsystem > §b@PoloCloud §7| " +
            "§7Developed by: §bHttpMarco §7| " +
            "Date: §b19.01.2020 §7| " +
            "§7Version: §b" + this.version, LogType.EMPTY);
        this.logger.log(" ", LogType.EMPTY);
        this.commandManager = new SimpleCommandManager();

        // copy wrapper and plugin jar
        ErrorHandler.defaultInstance().runOnly(() -> {
            final var storageDirectory = new File("storage/jars");
            storageDirectory.mkdirs();

            var loader = this.getClass().getClassLoader();
            var copyOption = StandardCopyOption.REPLACE_EXISTING;

            Files.copy(Objects.requireNonNull(loader.getResourceAsStream("wrapper.jar")), new File(storageDirectory, "wrapper.jar").toPath(), copyOption);
            Files.copy(Objects.requireNonNull(loader.getResourceAsStream("plugin.jar")), new File(storageDirectory, "plugin.jar").toPath(), copyOption);
            return null;
        });

        this.databaseManager = new DatabaseManager(this.config.getDatabaseConfiguration());
        this.groupManager = new SimpleGroupManager();
        this.serviceManager = new ServiceManager();
        this.groupTemplateService = new GroupTemplateService();
        this.playerManager = new SimplePlayerManager();
        this.node = new BaseNode(this.config);

        // register commands
        this.commandManager.registerCommands(
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
        this.logger.log("               ", LogType.EMPTY);
        this.logger.log("§7The cloud was successfully started.", LogType.SUCCESS);
        this.logger.log("               ", LogType.EMPTY);

        ((SimpleLogger) this.logger).getConsoleManager().start();

        this.queueService.checkForQueue();
    }

    private void loadConfig(@NotNull File file) {
        if (file.exists()) {
            this.config = new Document(file).get(CloudConfiguration.class);
            return;
        }
        new Document(this.config = new CloudConfiguration()).write(file);
    }

    public void onShutdown() {
        if (!this.running) return;
        this.running = false;
        this.logger.log("Trying to terminate cloudsystem.");
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
                this.logger.log("Successfully shutdown the cloudsystem.", LogType.SUCCESS);
                ((SimpleLogger) this.logger).getConsoleManager().shutdownReading();
                System.exit(0);
            });
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

}
