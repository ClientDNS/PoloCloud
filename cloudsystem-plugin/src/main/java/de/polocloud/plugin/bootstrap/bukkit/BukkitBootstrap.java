package de.polocloud.plugin.bootstrap.bukkit;

import de.polocloud.api.service.ServiceState;
import de.polocloud.wrapper.Wrapper;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitBootstrap extends JavaPlugin {

    @Override
    public void onEnable() {
        // update that the service is ready to use
        final var service = Wrapper.getInstance().thisService();

        if (service.getGroup().isAutoUpdating()) {
            service.setState(ServiceState.ONLINE);
            service.update();
        }
    }

}
