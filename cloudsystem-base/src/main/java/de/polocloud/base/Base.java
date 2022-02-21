package de.polocloud.base;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.CloudAPIType;
import de.polocloud.api.groups.GroupManager;
import de.polocloud.api.json.Document;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.logger.Logger;
import de.polocloud.api.player.PlayerManager;
import de.polocloud.api.service.ServiceManager;
import de.polocloud.base.command.CommandManager;
import de.polocloud.base.command.SimpleCommandManager;
import de.polocloud.base.command.defaults.*;
import de.polocloud.base.config.CloudConfiguration;
import de.polocloud.base.exception.DefaultExceptionCodes;
import de.polocloud.base.exception.ErrorHandler;
import de.polocloud.base.group.SimpleGroupManager;
import de.polocloud.base.logger.SimpleLogger;
import de.polocloud.base.node.BaseNode;
import de.polocloud.base.player.SimplePlayerManager;
import de.polocloud.base.service.LocalService;
import de.polocloud.base.service.SimpleServiceManager;
import de.polocloud.base.templates.GroupTemplateService;
import de.polocloud.database.DatabaseManager;
import lombok.Getter;
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
    private final DatabaseManager databaseManager;
    private final GroupManager groupManager;
    private final ServiceManager serviceManager;
    private final PlayerManager playerManager;
    private final GroupTemplateService groupTemplateService;
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
        final var storageDirectory = new File("storage/jars");
        final var wrapperPath = new File(storageDirectory, "wrapper.jar").toPath();

        try {
            storageDirectory.mkdirs();

            var loader = this.getClass().getClassLoader();
            var copyOption = StandardCopyOption.REPLACE_EXISTING;

            Files.copy(Objects.requireNonNull(loader.getResourceAsStream("wrapper.jar")), wrapperPath, copyOption);
            Files.copy(Objects.requireNonNull(loader.getResourceAsStream("plugin.jar")), new File(storageDirectory, "plugin.jar").toPath(), copyOption);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.databaseManager = DatabaseManager.newInstance(this.config.getDatabaseConfiguration());
        this.groupManager = new SimpleGroupManager();
        this.serviceManager = new SimpleServiceManager(wrapperPath);
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
            new ShutdownCommand(),
            new ScreenCommand());

        // add a shutdown hook for fast closes
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));

        // print finish successfully message
        this.logger.log("               ", LogType.EMPTY);
        this.logger.log("§7The cloud was successfully started.", LogType.SUCCESS);
        this.logger.log("               ", LogType.EMPTY);

        ((SimpleLogger) this.logger).getConsoleManager().start();

        new WorkerThread(this).start();
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
        ((SimpleLogger) this.logger).getConsoleManager().shutdownReading();
        this.serviceManager.getAllCachedServices()
            .forEach(service -> {
                if (service instanceof LocalService localService) localService.stop();
            });

        // delete wrapper and plugin jars
        try {
            final var storageDirectory = new File("storage/jars");

            Files.deleteIfExists(((SimpleServiceManager) this.getServiceManager()).getWrapperPath());
            Files.deleteIfExists(new File(storageDirectory, "plugin.jar").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.node.close();
        this.databaseManager.close();
        this.logger.log("Successfully shutdown the cloudsystem.", LogType.SUCCESS);
        System.exit(0);
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
