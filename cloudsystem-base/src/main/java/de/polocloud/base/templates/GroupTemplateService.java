package de.polocloud.base.templates;

import de.polocloud.base.Base;
import de.polocloud.api.groups.ServiceGroup;
import de.polocloud.api.service.CloudService;
import de.polocloud.base.service.LocalService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public final class GroupTemplateService {

    private final File everyFolder = new File("templates/EVERY");
    private final File everyServiceFolder = new File("templates/EVERY_SERVICE");
    private final File everyProxyFolder = new File("templates/EVERY_PROXY");

    public GroupTemplateService() {
        try {
            FileUtils.forceMkdir(this.everyFolder);
            FileUtils.forceMkdir(this.everyServiceFolder);
            FileUtils.forceMkdir(this.everyProxyFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyTemplates(CloudService service) throws IOException {
        final var serviceFolder = ((LocalService) service).getWorkingDirectory();
        FileUtils.copyDirectory(this.everyFolder, serviceFolder);
        FileUtils.copyDirectory(service.getGroup().getGameServerVersion().isProxy()
            ? this.everyProxyFolder : this.everyServiceFolder, serviceFolder);

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

}
