package de.bytemc.cloud.services.process.args;

import de.bytemc.cloud.api.groups.utils.ServiceTypes;
import de.bytemc.cloud.api.services.IService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarInputStream;

public final class ProcessJavaArgs {

    public static String[] args(final IService service) {
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
            "-cp", wrapperFile.toAbsolutePath() + File.pathSeparator + applicationFile.toPath().toAbsolutePath()));

        try (final JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(wrapperFile))) {
            arguments.add(jarInputStream.getManifest().getMainAttributes().getValue("Main-Class"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (final JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(applicationFile.toPath()))) {
            arguments.add(jarInputStream.getManifest().getMainAttributes().getValue("Main-Class"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if (service.getServiceGroup().getGameServerVersion().getServiceTypes() == ServiceTypes.SERVER) {
            arguments.add("nogui");
        }

        return arguments.toArray(new String[]{});
    }

}
