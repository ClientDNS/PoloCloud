package de.bytemc.cloud.api.player.impl;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.events.events.CloudPlayerUpdateEvent;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.services.IService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class SimpleCloudPlayer implements ICloudPlayer {

    private final UUID uniqueId;
    private final String username;
    private final IService proxyServer;
    private IService server;

    @Override
    public void update() {
        CloudAPI.getInstance().getCloudPlayerManager().updateCloudPlayer(this);
    }

    @Override
    public void update(@NotNull CloudPlayerUpdateEvent.UpdateReason updateReason) {
        CloudAPI.getInstance().getCloudPlayerManager().updateCloudPlayer(this, updateReason);
    }

}
