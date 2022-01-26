package de.bytemc.cloud.services.process;

import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.services.process.args.ProcessJavaArgs;
import de.bytemc.network.promise.CommunicationPromise;
import de.bytemc.network.promise.ICommunicationPromise;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class ProcessServiceStarter {

    private IService service;

    @SneakyThrows
    public ProcessServiceStarter(IService service) {
        this.service = service;
        this.service.setServiceState(ServiceState.STARTING);

        //create tmp file
        FileUtils.mkdir("tmp/" + service.getName());

        //load all current group templates
        //service.getServiceGroup().getTemplate()

        String jar = service.getServiceGroup().getGameServerVersion().getJar();
        FileUtils.copyFile(new File("storage/jars/" + jar), new File("tmp/" + service.getName() + "/" + jar));

        //copy plugin
        FileUtils.copyFile(new File("storage/jars/plugin.jar"), new File("tmp/" + service.getName() + "/plugins/plugin.jar"));
    }

    @SneakyThrows
    public ICommunicationPromise<IService> start() {
        var communicationPromise = new CommunicationPromise<IService>();
        String[] command = ProcessJavaArgs.args(service);

        ProcessBuilder processBuilder = new ProcessBuilder(command).directory(new File("tmp/" + service.getName() + "/"));
        Process process = processBuilder.start();

        Thread thread = new Thread(() -> {
            ((SimpleService) service).setProcess(process);
            communicationPromise.setSuccess(service);

            try {
                process.waitFor();

                //stop service

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return communicationPromise;
    }


}
