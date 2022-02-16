package de.polocloud.plugin.bootstrap.bukkit;

import de.polocloud.api.service.IService;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.api.service.utils.ServiceVisibility;
import de.polocloud.wrapper.Wrapper;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitBootstrap extends JavaPlugin {

    @Override
    public void onEnable() {
        // update that the service is ready to use
        final IService service = Wrapper.getInstance().thisService();

        if (service.getGroup().isAutoUpdating()) {
            service.setServiceVisibility(ServiceVisibility.VISIBLE);
            service.update();
        }
    }

    @Override
    public void onDisable() {
        Wrapper.getInstance().thisService().edit(service -> {
            service.setServiceState(ServiceState.STOPPING);
            service.setServiceVisibility(ServiceVisibility.INVISIBLE);
        });
    }

}
