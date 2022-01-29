package de.bytemc.cloud.services.ports;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;

public class PortHandler {

    private static final int PORTS_BOUNCE_PROXY = 25565;
    private static final int PORTS_BOUNCE = 30000;

    public static int getNextPort(IServiceGroup service) {
        int port = service.getGameServerVersion().isProxy() ? PORTS_BOUNCE_PROXY : PORTS_BOUNCE;
        while (isPortUsed(port)) {
            port++;
        }
        return port;
    }

    private static boolean isPortUsed(int port) {
        return CloudAPI.getInstance().getServiceManager().getAllCachedServices().stream().anyMatch(it -> it.getPort() == port);
    }

}
