package de.bytemc.cloud.api.player;

import de.bytemc.cloud.api.services.IService;

import java.util.UUID;

public interface ICloudPlayer {

    String getUsername();

    UUID getUniqueID();

    IService getProxyServer();

    IService getServer();

}
