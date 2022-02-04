package de.bytemc.cloud.plugin.bootstrap.spigot;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.utils.ServiceVisibility;
import de.bytemc.cloud.plugin.IPlugin;
import de.bytemc.cloud.wrapper.service.ServiceManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBootstrap extends JavaPlugin implements IPlugin {

    @Override
    public void onEnable() {
        //update that the service is ready to use
        IService service = ((ServiceManager) CloudAPI.getInstance().getServiceManager()).thisService();
        service.setServiceVisibility(ServiceVisibility.VISIBLE);
        service.update();
    }

    @Override
    public void shutdown() {
        Bukkit.getScheduler().runTask(this, Bukkit::shutdown);
    }

}
