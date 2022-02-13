package de.bytemc.cloud.api.groups.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.IServiceGroup;
import de.bytemc.cloud.api.versions.GameServerVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Getter
@Setter
@AllArgsConstructor
public class ServiceGroup implements IServiceGroup {

    private String name, template, node, motd;
    private int memory, defaultMaxPlayers, minOnlineService, maxOnlineService;
    private boolean staticService, fallbackGroup, maintenance, autoUpdating;
    private GameServerVersion gameServerVersion;

    @Override
    public void edit(final @NotNull Consumer<IServiceGroup> serviceGroupConsumer) {
        serviceGroupConsumer.accept(this);
        this.update();
    }

    @Override
    public void update() {
        CloudAPI.getInstance().getGroupManager().updateServiceGroup(this);
    }

}
