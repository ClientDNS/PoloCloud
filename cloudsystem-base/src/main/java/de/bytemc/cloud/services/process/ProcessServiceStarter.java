package de.bytemc.cloud.services.process;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.common.ConfigSplitSpacer;
import de.bytemc.cloud.api.common.ConfigurationFileEditor;
import de.bytemc.cloud.api.groups.utils.ServiceTypes;
import de.bytemc.cloud.api.json.Document;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.services.properties.BungeeProperties;
import de.bytemc.cloud.services.properties.SpigotProperties;
import de.bytemc.cloud.services.statistics.SimpleStatisticManager;
import de.bytemc.network.promise.CommunicationPromise;
import de.bytemc.network.promise.ICommunicationPromise;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public record ProcessServiceStarter(IService service) {

    @SneakyThrows
    public ProcessServiceStarter(final IService service) {
        this.service = service;
        this.service.setServiceState(ServiceState.STARTING);

        // add statistic to service
        SimpleStatisticManager.registerStartingProcess(this.service);

        this.service.getServiceGroup().getGameServerVersion().download();

        // create tmp file
        final File tmpFolder = new File("tmp/" + service.getName());
        FileUtils.forceMkdir(tmpFolder);

        // load all current group templates
        Base.getInstance().getGroupTemplateService().copyTemplates(service);

        final String jar = service.getServiceGroup().getGameServerVersion().getJar();
        FileUtils.copyFile(new File("storage/jars/" + jar), new File(tmpFolder, jar));

        // copy plugin
        FileUtils.copyFile(new File("storage/jars/plugin.jar"), new File(tmpFolder, "plugins/plugin.jar"));

        // write property for identify service
        new Document()
            .set("service", service.getName())
            .set("node", service.getServiceGroup().getNode())
            .set("hostname", Base.getInstance().getNode().getHostName())
            .set("port", Base.getInstance().getNode().getPort())
            .write(new File(tmpFolder, "property.json"));

        // check properties and modify
        if (service.getServiceGroup().getGameServerVersion().isProxy()) {
            final var file = new File(tmpFolder, "config.yml");
            if (file.exists()) {
                var editor = new ConfigurationFileEditor(file, ConfigSplitSpacer.YAML);
                editor.setValue("host", "0.0.0.0:" + service.getPort());
                editor.saveFile();
            } else new BungeeProperties(tmpFolder, service.getPort());
        } else {
            final var file = new File(tmpFolder, "server.properties");
            if (file.exists()) {
                var editor = new ConfigurationFileEditor(file, ConfigSplitSpacer.PROPERTIES);
                editor.setValue("server-port", String.valueOf(service.getPort()));
                editor.saveFile();
            } else new SpigotProperties(tmpFolder, service.getPort());
        }
    }

    @SneakyThrows
    public ICommunicationPromise<IService> start() {
        final var communicationPromise = new CommunicationPromise<IService>();
        final var command = this.arguments(this.service);

        final var processBuilder = new ProcessBuilder(command).directory(new File("tmp/" + this.service.getName() + "/"));
        processBuilder.redirectOutput(new File("tmp/" + this.service.getName() + "/wrapper.log"));

        ((SimpleService) this.service).setProcess(processBuilder.start());
        communicationPromise.setSuccess(this.service);
        return communicationPromise;
    }

    public String[] arguments(final IService service) {
        final List<String> arguments = new ArrayList<>(Arrays.asList(
            "java",
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
            "-Dcom.mojang.eula.agree=true",
            "-Dio.netty.recycler.maxCapacity=0",
            "-Dio.netty.recycler.maxCapacity.default=0",
            "-Djline.terminal=jline.UnsupportedTerminal",
            "-Xms" + service.getServiceGroup().getMemory() + "M",
            "-Xmx" + service.getServiceGroup().getMemory() + "M"));

        final var wrapperFile = Paths.get("storage", "jars", "wrapper.jar");
        final var applicationFile = new File("tmp/" + service.getName() + "/"
            + service.getServiceGroup().getGameServerVersion().getJar());

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

        if (service.getServiceGroup().getGameServerVersion().getServiceTypes() == ServiceTypes.SERVER) {
            arguments.add("nogui");
        }

        return arguments.toArray(new String[]{});
    }

}
