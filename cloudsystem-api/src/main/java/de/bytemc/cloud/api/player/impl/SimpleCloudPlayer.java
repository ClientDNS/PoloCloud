package de.bytemc.cloud.api.player.impl;

import de.bytemc.cloud.api.CloudAPI;
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
    private IService server;
    private IService proxyServer;

    @Override
    public void update() {
        CloudAPI.getInstance().getCloudPlayerManager().updateCloudPlayer(this);
    }
}
