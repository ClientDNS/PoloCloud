package de.bytemc.cloud.services;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.common.ConfigSplitSpacer;
import de.bytemc.cloud.api.common.ConfigurationFileEditor;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.groups.utils.ServiceType;
import de.bytemc.cloud.api.json.Document;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.cloud.services.properties.BungeeProperties;
import de.bytemc.cloud.services.properties.MinecraftProperties;
import de.bytemc.cloud.services.statistics.SimpleStatisticManager;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.promise.CommunicationPromise;
import de.bytemc.network.promise.ICommunicationPromise;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

@Getter
@Setter
public class LocalService implements IService {

    private final IServiceGroup group;
    private final int serviceId;
    private final String node;
    private final int port;
    private final String hostName;

    private int maxPlayers;
    private String motd;

    private ServiceState serviceState = ServiceState.PREPARED;
    private ServiceVisibility serviceVisibility = ServiceVisibility.BLANK;

    private final File workingDirectory;

    private Process process;

    public LocalService(final IServiceGroup group, final int id, final int port, final String hostname) {
        this.group = group;
        this.serviceId = id;
        this.node = Base.getInstance().getNode().getNodeName();
        this.port = port;
        this.hostName = hostname;
        assert this.group != null;
        this.motd = this.group.getMotd();
        this.maxPlayers = this.group.getDefaultMaxPlayers();

        this.workingDirectory = new File((!this.group.isStatic() ? "tmp" : "static") +"/" + this.getName());
    }

    @SneakyThrows
    public ICommunicationPromise<IService> start() {
        this.setServiceState(ServiceState.STARTING);

        // add statistic to service
        SimpleStatisticManager.registerStartingProcess(this);

        this.group.getGameServerVersion().download();

        // create tmp file
        FileUtils.forceMkdir(this.workingDirectory);

        // load all current group templates
        Base.getInstance().getGroupTemplateService().copyTemplates(this);

        final var storageFolder = new File("storage/jars");

        final var jar = this.group.getGameServerVersion().getJar();
        FileUtils.copyFile(new File(storageFolder, jar), new File(this.workingDirectory, jar));

        // copy plugin
        FileUtils.copyFile(new File(storageFolder, "/plugin.jar"), new File(this.workingDirectory, "plugins/plugin.jar"));

        // write property for identify service
        new Document()
            .set("service", this.getName())
            .set("node", this.node)
            .set("hostname", Base.getInstance().getNode().getHostName())
            .set("port", Base.getInstance().getNode().getPort())
            .write(new File(this.workingDirectory, "property.json"));

        // check properties and modify
        if (this.group.getGameServerVersion().isProxy()) {
            final var file = new File(this.workingDirectory, "config.yml");
            if (file.exists()) {
                var editor = new ConfigurationFileEditor(file, ConfigSplitSpacer.YAML);
                editor.setValue("host", "0.0.0.0:" + this.port);
                editor.saveFile();
            } else new BungeeProperties(this.workingDirectory, this.port);
        } else {
            final var properties = new Properties();
            final var file = new File(this.workingDirectory, "server.properties");
            if (file.exists()) {
                properties.setProperty("server-port", String.valueOf(this.port));
                try (final var fileWriter = new FileWriter(file)) {
                    properties.store(fileWriter, null);
                }
            } else new MinecraftProperties(this.workingDirectory, this.port);

            properties.clear();

            properties.setProperty("eula", "true");
            try (final var fileWriter = new FileWriter(new File(this.workingDirectory, "eula.txt"))) {
                properties.store(fileWriter, null);
            }
        }

        final var communicationPromise = new CommunicationPromise<IService>();
        final var processBuilder = new ProcessBuilder(this.arguments())
            .directory(this.workingDirectory);
        processBuilder.redirectOutput(new File(this.workingDirectory, "/wrapper.log"));

        this.process = processBuilder.start();
        communicationPromise.setSuccess(this);
        return communicationPromise;
    }

    @Override
    public @NotNull String getName() {
        return this.group.getName() + "-" + this.serviceId;
    }

    @Override
    public void edit(final @NotNull Consumer<IService> serviceConsumer) {
        serviceConsumer.accept(this);
        this.update();
    }

    public void update() {
        CloudAPI.getInstance().getServiceManager().updateService(this);
    }

    @Override
    public void sendPacket(@NotNull IPacket packet) {
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
        if (this.process != null) {
            this.executeCommand(this.group.getGameServerVersion().isProxy() ? "end" : "stop");
            try {
                if (this.process.waitFor(5, TimeUnit.SECONDS)) this.process = null;
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.process.destroy();
            this.process = null;
        }
    }

    public void delete() {
        if (this.group.isStatic()) return;
        try {
            FileUtils.deleteDirectory(this.workingDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> arguments() {
        final List<String> arguments = new ArrayList<>(Arrays.asList(
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
            "-DIReallyKnowWhatIAmDoingISwear=true",
            "-Xms" + this.group.getMemory() + "M",
            "-Xmx" + this.group.getMemory() + "M"));

        final var wrapperFile = Paths.get("storage", "jars", "wrapper.jar");
        final var applicationFile = new File(this.workingDirectory, this.group.getGameServerVersion().getJar());

        arguments.addAll(Arrays.asList(
            "-cp", wrapperFile.toAbsolutePath().toString(),
            "-javaagent:" + wrapperFile.toAbsolutePath()));

        try (final JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(wrapperFile))) {
            arguments.add(jarInputStream.getManifest().getMainAttributes().getValue("Main-Class"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean preLoadClasses = false;

        try (final JarFile jarFile = new JarFile(applicationFile)) {
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

}
