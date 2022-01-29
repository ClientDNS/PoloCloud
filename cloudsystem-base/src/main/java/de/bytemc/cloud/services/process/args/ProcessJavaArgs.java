package de.bytemc.cloud.services.process.args;

import de.bytemc.cloud.api.groups.utils.ServiceTypes;
import de.bytemc.cloud.api.services.IService;

public class ProcessJavaArgs {

    public static String[] args(IService service) {
        return new String[]{
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
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=50",
            "-XX:-UseAdaptiveSizePolicy",
            "-XX:CompileThreshold=100",
            "-Dcom.mojang.eula.agree=true",
            "-Dio.netty.recycler.maxCapacity=0",
            "-Dio.netty.recycler.maxCapacity.default=0",
            "-Djline.terminal=jline.UnsupportedTerminal",
            "-Xmx" + service.getServiceGroup().getMemory() + "M",
            "-jar",
            service.getServiceGroup().getGameServerVersion().getJar(),
            service.getServiceGroup().getGameServerVersion().getServiceTypes() == ServiceTypes.SERVER ? "nogui" : ""
        };
    }

}
