package de.polocloud.api.player.impl;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.service.IService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public final class SimpleCloudPlayer implements ICloudPlayer {

    private final UUID uniqueId;
    private final String username;
    private final IService proxyServer;
    private IService server;

    @Override
    public void update() {
        CloudAPI.getInstance().getPlayerManager().updateCloudPlayer(this);
    }

    @Override
    public void update(@NotNull CloudPlayerUpdateEvent.UpdateReason updateReason) {
        CloudAPI.getInstance().getPlayerManager().updateCloudPlayer(this, updateReason);
    }

}
