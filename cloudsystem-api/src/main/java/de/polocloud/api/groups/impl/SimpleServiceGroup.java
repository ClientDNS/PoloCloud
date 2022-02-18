package de.polocloud.api.groups.impl;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.version.GameServerVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Getter
@Setter
@AllArgsConstructor
public class SimpleServiceGroup implements de.polocloud.api.groups.ServiceGroup {

    private String name, template, node, motd;
    private int maxMemory, defaultMaxPlayers, minOnlineService, maxOnlineService;
    private boolean isStatic, fallbackGroup, maintenance, autoUpdating;
    private GameServerVersion gameServerVersion;

    @Override
    public void edit(final @NotNull Consumer<de.polocloud.api.groups.ServiceGroup> serviceGroupConsumer) {
        serviceGroupConsumer.accept(this);
        this.update();
    }

    @Override
    public void update() {
        CloudAPI.getInstance().getGroupManager().updateServiceGroup(this);
    }

}
