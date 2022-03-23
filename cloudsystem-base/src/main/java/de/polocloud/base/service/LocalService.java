package de.polocloud.base.service;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.groups.utils.ServiceType;
import de.polocloud.api.json.Document;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.service.ServiceState;
import de.polocloud.api.version.GameServerVersion;
import de.polocloud.base.Base;
import de.polocloud.base.config.editor.ConfigurationFileEditor;
import de.polocloud.base.service.statistic.SimpleStatisticManager;
import de.polocloud.network.packet.Packet;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.jar.JarFile;

@Getter
@Setter
public class LocalService implements CloudService {

    private final UUID uuid;
    private final ServiceGroup group;
    private final int serviceId;
    private final String node;
    private final int port;
    private final String hostName;

    private int maxPlayers;
    private String motd;

    private String state = ServiceState.PREPARED;

    private final File workingDirectory;

    private Process process;

    private boolean screen = false;

    public LocalService(final ServiceGroup group, final int id, final int port, final String hostname) {
        this.uuid = UUID.randomUUID();
        this.group = group;
        this.serviceId = id;
        this.node = Base.getInstance().getNode().getName();
        this.port = port;
        this.hostName = hostname;
        assert this.group != null;
        this.motd = this.group.getMotd();
        this.maxPlayers = this.group.getDefaultMaxPlayers();

        if (this.group.isStatic()) {
            this.workingDirectory = new File("static/" + this.getName());
        } else {
            this.workingDirectory = new File("tmp/" + this.getName() + "." + this.uuid);
        }
    }

    @SneakyThrows
    public void start() {
        this.setState(ServiceState.STARTING);

        this.downloadVersion(this.group.getGameServerVersion());

        // add statistic to service
        SimpleStatisticManager.registerStartingProcess(this);

        // create working directory
        this.workingDirectory.mkdirs();

        // load all current group templates
        Base.getInstance().getGroupTemplateService().copyTemplates(this);

        final var storageFolder = new File("storage/jars");

        final var jar = this.group.getGameServerVersion().getJar();
        Files.copy(new File(storageFolder, jar).toPath(), new File(this.workingDirectory, jar).toPath(),
            StandardCopyOption.REPLACE_EXISTING);

        // copy plugin
        final var pluginDirectory = new File(this.workingDirectory, "plugins");
        pluginDirectory.mkdir();
        Files.copy(((SimpleServiceManager) Base.getInstance().getServiceManager()).getPluginPath(),
            new File(pluginDirectory, "plugin.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);

        // write property for identify service
        new Document()
            .set("service", this.getName())
            .set("node", this.node)
            .set("hostname", Base.getInstance().getNode().getHostName())
            .set("port", Base.getInstance().getNode().getPort())
            .write(new File(this.workingDirectory, "property.json"));

        // check properties and modify
        if (this.group.getGameServerVersion() == GameServerVersion.WATERFALL) {
            final var file = new File(this.workingDirectory, "config.yml");
            if (!file.exists()) {
                try (final var inputStream = this.getClass().getClassLoader().getResourceAsStream("defaultFiles/config.yml")) {
                    assert inputStream != null;
                    FileUtils.copyToFile(inputStream, file);
                }
            }
            new ConfigurationFileEditor(file, s -> {
                if (s.startsWith("  host: ")) {
                    return "  host: 0.0.0.0:" + this.port;
                } else {
                    return s;
                }
            });
        } else if (this.group.getGameServerVersion() == GameServerVersion.VELOCITY) {
            final var file = new File(this.workingDirectory, "velocity.toml");
            if (!file.exists()) {
                try (final var inputStream = this.getClass().getClassLoader().getResourceAsStream("defaultFiles/velocity.toml")) {
                    assert inputStream != null;
                    FileUtils.copyToFile(inputStream, file);
                }
            }
            new ConfigurationFileEditor(file, s -> {
                if (s.startsWith("bind = ")) {
                    return "bind = \"0.0.0.0:" + this.port + "\"";
                } else {
                    return s;
                }
            });
        } else {
            final var properties = new Properties();
            final var file = new File(this.workingDirectory, "server.properties");
            if (file.exists()) {
                try (final var fileReader = new FileReader(file)) {
                    properties.load(fileReader);
                }
            } else {
                try (final var inputStreamReader = new InputStreamReader(
                    Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("defaultFiles/server.properties")))) {
                    properties.load(inputStreamReader);
                }
            }
            properties.setProperty("server-name", this.getName());
            properties.setProperty("server-port", String.valueOf(this.port));
            try (final var fileWriter = new FileWriter(file)) {
                properties.store(fileWriter, null);
            }

            properties.clear();

            properties.setProperty("eula", "true");
            try (final var fileWriter = new FileWriter(new File(this.workingDirectory, "eula.txt"))) {
                properties.store(fileWriter, null);
            }
        }

        this.process = new ProcessBuilder(this.arguments())
            .directory(this.workingDirectory)
            .start();
    }

    @Override
    public @NotNull String getName() {
        return this.group.getName() + "-" + this.serviceId;
    }

    @Override
    public void edit(final @NotNull Consumer<CloudService> serviceConsumer) {
        serviceConsumer.accept(this);
        this.update();
    }

    public void update() {
        CloudAPI.getInstance().getServiceManager().updateService(this);
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        CloudAPI.getInstance().getServiceManager().sendPacketToService(this, packet);
    }

    @Override
    public void executeCommand(@NotNull String command) {
        if (this.process != null) {
            final var outputStream = this.process.getOutputStream();
            try {
                outputStream.write((command + "\n").getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        this.stopProcess();
        this.delete();
    }

    private void stopProcess() {
        if (this.process != null) {
            this.executeCommand(this.group.getGameServerVersion().isProxy() ? "end" : "stop");
            try {
                if (this.process.waitFor(5, TimeUnit.SECONDS)) {
                    this.process = null;
                    return;
                }
            } catch (InterruptedException ignored) {
            }
            this.process.toHandle().destroyForcibly();
            this.process = null;
        }
    }

    private void delete() {
        if (this.group.isStatic()) return;
        synchronized (this) {
            try {
                FileUtils.deleteDirectory(this.workingDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> arguments() {
        final var arguments = new ArrayList<>(Arrays.asList(
            Base.getInstance().getConfig().getJavaCommand(),
            "-XX:+UseG1GC",
            "-XX:+ParallelRefProcEnabled",
            "-XX:MaxGCPauseMillis=200",
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+DisableExplicitGC",
            "-XX:+AlwaysPreTouch",
            "-XX:G1NewSizePercent=30",
            "-XX:G1MaxNewSizePercent=40",
            "-XX:G1HeapRegionSize=8M",
            "-XX:G1ReservePercent=20",
            "-XX:G1HeapWastePercent=5",
            "-XX:G1MixedGCCountTarget=4",
            "-XX:InitiatingHeapOccupancyPercent=15",
            "-XX:G1MixedGCLiveThresholdPercent=90",
            "-XX:G1RSetUpdatingPauseTimePercent=5",
            "-XX:SurvivorRatio=32",
            "-XX:+PerfDisableSharedMem",
            "-XX:MaxTenuringThreshold=1",
            "-Dusing.aikars.flags=https://mcflags.emc.gs",
            "-Daikars.new.flags=true",
            "-XX:-UseAdaptiveSizePolicy",
            "-XX:CompileThreshold=100",
            "-Dio.netty.recycler.maxCapacity=0",
            "-Dio.netty.recycler.maxCapacity.default=0",
            "-Djline.terminal=jline.UnsupportedTerminal",
            "-Dfile.encoding=UTF-8",
            "-Dclient.encoding.override=UTF-8",
            "-DIReallyKnowWhatIAmDoingISwear=true",
            "-Xms" + this.group.getMaxMemory() + "M",
            "-Xmx" + this.group.getMaxMemory() + "M"));

        arguments.addAll(Base.getInstance().getConfig().getJvmFlags());

        final var serviceManager = (SimpleServiceManager) Base.getInstance().getServiceManager();
        final var applicationFile = new File(this.workingDirectory, this.group.getGameServerVersion().getJar());

        arguments.addAll(Arrays.asList(
            "-cp", serviceManager.getWrapperPath().toString(),
            "-javaagent:" + serviceManager.getWrapperPath()));

        arguments.add(serviceManager.getWrapperMainClass());

        var preLoadClasses = false;

        try (final var jarFile = new JarFile(applicationFile)) {
            arguments.add(jarFile.getManifest().getMainAttributes().getValue("Main-Class"));
            preLoadClasses = jarFile.getEntry("META-INF/versions.list") != null;
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        arguments.add(applicationFile.toPath().toAbsolutePath().toString());

        arguments.add(Boolean.toString(preLoadClasses));

        if (this.group.getGameServerVersion().getServiceTypes() == ServiceType.SERVER) {
            arguments.add("nogui");
        }

        return arguments;
    }

    public boolean isScreen() {
        return this.screen;
    }

    public void setScreen(final boolean screen) {
        this.screen = screen;
    }

    private void downloadVersion(final GameServerVersion gameServerVersion) {
        final var directory = new File("storage/jars");
        final var file = new File(directory, gameServerVersion.getJar());

        if (file.exists()) return;

        CloudAPI.getInstance().getLogger().log("§7Downloading §bVersion§7... (§3" + this.getName() + "§7)");

        file.getParentFile().mkdirs();
        try {
            var url = gameServerVersion.getUrl();
            FileUtils.copyURLToFile(new URL(url), file);

            if (gameServerVersion.getTitle().equals("paper")) {
                final var process = new ProcessBuilder("java", "-Dpaperclip.patchonly=true", "-jar", gameServerVersion.getJar())
                    .directory(file.getParentFile()).start();
                final var inputStreamReader = new InputStreamReader(process.getInputStream());
                final var bufferedReader = new BufferedReader(inputStreamReader);
                process.waitFor();
                process.destroyForcibly();
                bufferedReader.close();
                inputStreamReader.close();
                final var cacheDirectory = new File(directory, "cache");
                final var patchedJar = new File(cacheDirectory, "/patched_" + gameServerVersion.getVersion() + ".jar");
                if (patchedJar.exists()) {
                    Files.copy(patchedJar.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    final var templateDirectory = new File("templates/" + this.group.getTemplate());
                    final var versionsDirectory = new File(directory, "versions");
                    final var librariesDirectory = new File(directory, "libraries");
                    FileUtils.copyDirectory(versionsDirectory, new File(templateDirectory, "versions"));
                    FileUtils.copyDirectory(librariesDirectory, new File(templateDirectory, "libraries"));
                    FileUtils.copyDirectory(cacheDirectory, new File(templateDirectory, "cache"));
                    FileUtils.deleteDirectory(versionsDirectory);
                    FileUtils.deleteDirectory(librariesDirectory);
                    FileUtils.deleteDirectory(cacheDirectory);
                }
                FileUtils.deleteDirectory(new File(directory, "cache"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            CloudAPI.getInstance().getLogger().log("§cFailed to download version§7... (§3" + this.getName() + "§7)", LogType.ERROR);
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CloudAPI.getInstance().getLogger().log("§7Downloading of (§3" + this.getName() + "§7)§a successfully §7completed.");
    }

}
