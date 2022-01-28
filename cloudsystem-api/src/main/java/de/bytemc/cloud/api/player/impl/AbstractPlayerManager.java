package de.bytemc.cloud.api.player.impl;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.player.ICloudPlayer;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class AbstractPlayerManager implements ICloudPlayerManager {

    private final List<ICloudPlayer> allCachedCloudPlayers = Lists.newArrayList();

}
