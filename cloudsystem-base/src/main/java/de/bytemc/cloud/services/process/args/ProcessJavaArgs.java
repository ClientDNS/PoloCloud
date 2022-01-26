package de.bytemc.cloud.services.process.args;

import de.bytemc.cloud.api.groups.utils.ServiceTypes;
import de.bytemc.cloud.api.services.IService;

public class ProcessJavaArgs {

    public static String[] args(IService service) {
        return new String[]{
            "java",
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
