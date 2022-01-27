package de.bytemc.cloud.services.process;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.common.ConfigSplitSpacer;
import de.bytemc.cloud.api.common.ConfigurationFileEditor;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.services.process.args.ProcessJavaArgs;
import de.bytemc.cloud.services.process.file.PropertyFileWriter;
import de.bytemc.cloud.services.properties.BungeeProperties;
import de.bytemc.cloud.services.properties.SpigotProperties;
import de.bytemc.network.promise.CommunicationPromise;
import de.bytemc.network.promise.ICommunicationPromise;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ProcessServiceStarter {

    private IService service;

    @SneakyThrows
    public ProcessServiceStarter(IService service) {
        this.service = service;
        this.service.setServiceState(ServiceState.STARTING);

        this.service.getServiceGroup().getGameServerVersion().download();

        //create tmp file
        FileUtils.forceMkdir(new File("tmp/" + service.getName() + "/"));

        //load all current group templates
        Base.getInstance().getGroupTemplateService().copyTemplates(service);

        String jar = service.getServiceGroup().getGameServerVersion().getJar();
        FileUtils.copyFile(new File("storage/jars/" + jar), new File("tmp/" + service.getName() + "/" + jar));

        //copy plugin
        FileUtils.copyFile(new File("storage/jars/plugin.jar"), new File("tmp/" + service.getName() + "/plugins/plugin.jar"));

        //write property for identify service
        new PropertyFileWriter(service);

        //check properties and modify
        if(service.getServiceGroup().getGameServerVersion().isProxy()) {
            //TODO OPTIMIZE
            var file = new File("tmp/" + service.getName() + "/config.yml");
            if(file.exists()) {
                var editor = new ConfigurationFileEditor(file, ConfigSplitSpacer.YAML);
                editor.setValue("host", "0.0.0.0:" + service.getPort());
                editor.saveFile();
            }else new BungeeProperties(new File("tmp/" + service.getName() + "/"), service.getPort());
        } else {
            var file = new File("tmp/" + service.getName() + "/config.properties");
            if(file.exists()) {
                //TODO
            } else new SpigotProperties(new File("tmp/" + service.getName() + "/config.properties"), service.getPort());
        }
    }

    @SneakyThrows
    public ICommunicationPromise<IService> start() {
        var communicationPromise = new CommunicationPromise<IService>();
        var command = ProcessJavaArgs.args(service);

        var processBuilder = new ProcessBuilder(command).directory(new File("tmp/" + service.getName() + "/"));
        var process = processBuilder.start();

        var thread = new Thread(() -> {
            ((SimpleService) service).setProcess(process);
            communicationPromise.setSuccess(service);

            try {
                process.waitFor();

                //stop service
                FileUtils.deleteDirectory(new File("tmp/" + service.getName() + "/"));
                CloudAPI.getInstance().getLoggerProvider().logMessage("The service '§b" + service.getName() + "§7' is now successfully offline.");
                CloudAPI.getInstance().getServiceManager().getAllCachedServices().remove(service);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return communicationPromise;
    }


}
