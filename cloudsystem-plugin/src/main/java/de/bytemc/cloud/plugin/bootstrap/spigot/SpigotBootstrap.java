package de.bytemc.cloud.plugin.bootstrap.spigot;

import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.cloud.wrapper.Wrapper;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBootstrap extends JavaPlugin {

    @Override
    public void onEnable() {
        //update that the service is ready to use
        IService service = Wrapper.getInstance().thisService();

        if(service.getGroup().isAutoUpdating()) {
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
