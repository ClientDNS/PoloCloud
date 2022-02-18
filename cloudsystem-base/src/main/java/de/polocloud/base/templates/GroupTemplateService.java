package de.polocloud.base.templates;

import de.polocloud.base.Base;
import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.service.CloudService;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class GroupTemplateService {

    private final String everyFolder = "templates/EVERY/";
    private final String everyServiceFolder = "templates/EVERY_SERVICE/";
    private final String everyProxyFolder = "templates/EVERY_PROXY/";

    public GroupTemplateService() {
        this.initFolder(this.everyFolder);
        this.initFolder(this.everyServiceFolder);
        this.initFolder(this.everyProxyFolder);
    }

    public void copyTemplates(CloudService service) throws IOException {
        final var serviceFolder = new File("tmp/" + service.getName() + "/");
        FileUtils.copyDirectory(new File(this.everyFolder), serviceFolder);
        FileUtils.copyDirectory(new File(service.getGroup().getGameServerVersion().isProxy()
            ? this.everyProxyFolder : this.everyServiceFolder), serviceFolder);

        final var templateDirection = new File("templates/" + service.getGroup().getTemplate());
        if (templateDirection.exists()) {
            FileUtils.copyDirectory(templateDirection, serviceFolder);
        }
    }

    public void createTemplateFolder(ServiceGroup group) {
        if (!group.getNode().equalsIgnoreCase(Base.getInstance().getNode().getName())) return;
        final var file = new File("templates/" + group.getTemplate());
        if (!file.exists()) //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
    }

    @SneakyThrows
    public void initFolder(String file) {
        FileUtils.forceMkdir(new File(file));
    }

}
