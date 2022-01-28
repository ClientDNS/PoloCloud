package de.bytemc.cloud.templates;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.services.IService;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class GroupTemplateService {

    private static final String EVERY_FOLDER = "templates/EVERY/";
    private static final String EVERY_SERVICE_FOLDER = "templates/EVERY_SERVICE/";
    private static final String EVERY_PROXY_FOLDER = "templates/EVERY_PROXY/";

    public GroupTemplateService() {
        initFolder(EVERY_FOLDER);
        initFolder(EVERY_SERVICE_FOLDER);
        initFolder(EVERY_PROXY_FOLDER);
    }

    public void copyTemplates(IService service) throws IOException {
        var serviceFolder = new File("tmp/" + service.getName() + "/");
        FileUtils.copyDirectory(new File(EVERY_FOLDER), serviceFolder);
        FileUtils.copyDirectory(new File(service.getServiceGroup().getGameServerVersion().isProxy() ? EVERY_PROXY_FOLDER : EVERY_SERVICE_FOLDER), serviceFolder);

        var templateDirection = new File("templates/" + service.getServiceGroup().getTemplate());
        if(templateDirection.exists()){
            FileUtils.copyDirectory(templateDirection, serviceFolder);
        }
    }

    public void createTemplateFolder(IServiceGroup group) {
        if(!group.getNode().equalsIgnoreCase(Base.getInstance().getNode().getNodeName())) return;
        var file = new File("templates/" + group.getTemplate());
        if(!file.exists()) file.mkdirs();
    }

    @SneakyThrows
    public void initFolder(String file) {
        FileUtils.forceMkdir(new File(file));
    }

}
