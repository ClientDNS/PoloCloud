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
import de.polocloud.base.console.SimpleConsoleManager;
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
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.jar.Manifest;

@Getter
public final class Base extends CloudAPI {

    @Getter
    private static Base instance;

    private final String version;

    private CloudConfiguration config;

    private CommandManager commandManager;
    private BaseNode node;
    private DatabaseManager databaseManager;
    private GroupManager groupManager;
    private ServiceManager serviceManager;
    private PlayerManager playerManager;
    private GroupTemplateService groupTemplateService;
    private WorkerThread workerThread;
    private boolean running = true;

    public Base() {
        super(CloudAPIType.NODE);

        instance = this;

        var date = "19.01.2020";
        try (final var stream = this.getClass().getClassLoader().getResources("META-INF/MANIFEST.MF")
            .nextElement().openStream()) {
            var manifest = new Manifest(stream);
            if (manifest.getMainAttributes().getValue("version-date") != null) {
                date = manifest.getMainAttributes().getValue("version-date");
            }
        } catch (IOException ignored) {}

        this.version = this.getClass().getPackage().getImplementationVersion();

        this.logger = new SimpleLogger();

        ErrorHandler.defaultInstance().registerDefaultThreadExceptionHandler()
            .orElse((throwable, errorHandler) -> {
                this.getLogger().log("§7Caught an §cunexpected error §7(§c" + throwable.getClass().getSimpleName() + "§7) " +
                    "| (§b" + throwable.getMessage() + "§7)", LogType.ERROR);
                throwable.printStackTrace();
            });

        this.logger.log("§7Cloudsystem > §b@PoloCloud §7| " +
            "§7Developed by: §bHttpMarco §7| " +
            "Date: §b" + (date.equalsIgnoreCase("19.01.2020") ? date : (date + " §7(Since §b19.01.2020§7)")) + " §7| " +
            "§7Version: §b" + this.version, LogType.EMPTY);
        this.logger.log(" ", LogType.EMPTY);

        if (this.loadConfig(new File("config.json"))) {
            this.logger.log("§cPlease configure your database in the '§bconfig.json§7'!", LogType.WARNING);
            return;
        }
        this.checkVersion();

        new DefaultExceptionCodes();

        this.commandManager = new SimpleCommandManager();

        this.databaseManager = DatabaseManager.newInstance(this.config.getDatabaseConfiguration());
        this.workerThread = new WorkerThread(this);
        this.groupManager = new SimpleGroupManager();
        this.serviceManager = new SimpleServiceManager();
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
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown, "PoloCloud-Shutdown-Thread"));

        // print finish successfully message
        this.logger.log("               ", LogType.EMPTY);
        this.logger.log("§7The cloud was successfully started.", LogType.SUCCESS);
        this.logger.log("               ", LogType.EMPTY);

        ((SimpleLogger) this.logger).getConsoleManager().start();

        this.workerThread.start();
    }

    private boolean loadConfig(@NotNull File file) {
        if (file.exists()) {
            this.config = new Document(file).get(CloudConfiguration.class);
            return false;
        }
        new Document(this.config = new CloudConfiguration()).write(file);
        return true;
    }

    private void checkVersion() {
        if (this.config.isCheckForUpdate() && !this.version.endsWith("SNAPSHOT")) {
            try {
                final var url = new URL("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=96270");
                final var inputStream = url.openStream();
                final var inputStreamReader = new InputStreamReader(url.openStream());
                if (!this.version.equals(new Document(inputStreamReader).get("current_version", String.class))) {
                    this.logger.log("A newer version of the cloud is available.", LogType.WARNING);
                }
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onShutdown() {
        if (!this.running) return;
        this.running = false;
        this.logger.log("§7Trying to terminate the §bcloudsystem§7.");
        ((SimpleLogger) this.logger).getConsoleManager().shutdownReading();
        this.serviceManager.getAllCachedServices()
            .forEach(service -> {
                if (service instanceof LocalService localService) localService.stop();
            });

        try {
            // delete wrapper and plugin jars
            Files.deleteIfExists(((SimpleServiceManager) this.getServiceManager()).getWrapperPath());
            Files.deleteIfExists(((SimpleServiceManager) this.getServiceManager()).getPluginPath());

            // delete temporary directory
            FileUtils.deleteDirectory(new File("tmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.node.close();
        this.databaseManager.close();
        this.logger.log("§aSuccessfully §7stopped the §bcloudsystem§7.", LogType.SUCCESS);
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

    public SimpleConsoleManager getConsoleManager() {
        return ((SimpleLogger) this.logger).getConsoleManager();
    }

    public WorkerThread getWorkerThread() {
        return this.workerThread;
    }

}
