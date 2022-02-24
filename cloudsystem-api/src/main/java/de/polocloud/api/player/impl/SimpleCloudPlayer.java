package de.polocloud.api.player.impl;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.event.player.CloudPlayerUpdateEvent;
import de.polocloud.api.player.CloudPlayer;
import de.polocloud.api.service.CloudService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public final class SimpleCloudPlayer implements CloudPlayer {

    private final UUID uniqueId;
    private final String username;
    private final CloudService proxyServer;
    private CloudService server;

    @Override
    public void update() {
        CloudAPI.getInstance().getPlayerManager().updateCloudPlayer(this);
    }

    @Override
    public void update(@NotNull CloudPlayerUpdateEvent.UpdateReason updateReason) {
        CloudAPI.getInstance().getPlayerManager().updateCloudPlayer(this, updateReason);
    }

}


