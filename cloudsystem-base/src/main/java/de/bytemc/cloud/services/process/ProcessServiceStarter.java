package de.bytemc.cloud.services.process;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.common.ConfigSplitSpacer;
import de.bytemc.cloud.api.common.ConfigurationFileEditor;
import de.bytemc.cloud.api.json.Document;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.impl.SimpleService;
import de.bytemc.cloud.api.services.utils.ServiceState;
import de.bytemc.cloud.services.process.args.ProcessJavaArgs;
import de.bytemc.cloud.services.properties.BungeeProperties;
import de.bytemc.cloud.services.properties.SpigotProperties;
import de.bytemc.cloud.services.statistics.SimpleStatisticManager;
import de.bytemc.network.promise.CommunicationPromise;
import de.bytemc.network.promise.ICommunicationPromise;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public record ProcessServiceStarter(IService service) {

    @SneakyThrows
    public ProcessServiceStarter(final IService service) {
        this.service = service;
        this.service.setServiceState(ServiceState.STARTING);

        // add statistic to service
        SimpleStatisticManager.registerStartingProcess(this.service);

        this.service.getServiceGroup().getGameServerVersion().download();

        // create tmp file
        final File tmpFolder = new File("tmp/" + service.getName());
        FileUtils.forceMkdir(tmpFolder);

        // load all current group templates
        Base.getInstance().getGroupTemplateService().copyTemplates(service);

        final String jar = service.getServiceGroup().getGameServerVersion().getJar();
        FileUtils.copyFile(new File("storage/jars/" + jar), new File(tmpFolder, jar));

        // copy plugin
        FileUtils.copyFile(new File("storage/jars/plugin.jar"), new File(tmpFolder, "plugins/plugin.jar"));

        // write property for identify service
        new Document()
            .set("service", service.getName())
            .set("node", service.getServiceGroup().getNode())
            .set("hostname", Base.getInstance().getNode().getHostName())
            .set("port", Base.getInstance().getNode().getPort())
            .write(new File(tmpFolder, "property.json"));

        // check properties and modify
        if (service.getServiceGroup().getGameServerVersion().isProxy()) {
            final var file = new File(tmpFolder, "config.yml");
            if (file.exists()) {
                var editor = new ConfigurationFileEditor(file, ConfigSplitSpacer.YAML);
                editor.setValue("host", "0.0.0.0:" + service.getPort());
                editor.saveFile();
            } else new BungeeProperties(tmpFolder, service.getPort());
        } else {
            final var file = new File(tmpFolder, "server.properties");
            if (file.exists()) {
                var editor = new ConfigurationFileEditor(file, ConfigSplitSpacer.PROPERTIES);
                editor.setValue("server-port", String.valueOf(service.getPort()));
                editor.saveFile();
            } else new SpigotProperties(tmpFolder, service.getPort());
        }
    }

    @SneakyThrows
    public ICommunicationPromise<IService> start() {
        final var communicationPromise = new CommunicationPromise<IService>();
        final var command = ProcessJavaArgs.args(this.service);

        final var processBuilder = new ProcessBuilder(command).directory(new File("tmp/" + this.service.getName() + "/"));
        processBuilder.redirectOutput(new File("tmp/" + service.getName() + "/wrapper.log"));

        ((SimpleService) this.service).setProcess(processBuilder.start());
        communicationPromise.setSuccess(this.service);
        return communicationPromise;
    }


}
