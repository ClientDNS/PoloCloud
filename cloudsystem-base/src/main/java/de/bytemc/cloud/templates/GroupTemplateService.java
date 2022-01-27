package de.bytemc.cloud.templates;

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
        FileUtils.copyDirectory(new File(EVERY_FOLDER), new File("tmp/" + service.getName() + "/"));
        FileUtils.copyDirectory(new File(service.getServiceGroup().getGameServerVersion().isProxy() ? EVERY_PROXY_FOLDER : EVERY_SERVICE_FOLDER), new File("tmp/" + service.getName() + "/"));
    }

    @SneakyThrows
    public void initFolder(String file) {
        FileUtils.forceMkdir(new File(file));
    }

}
