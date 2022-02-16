package de.polocloud.api.event.player;

import de.polocloud.api.event.ICloudEvent;
import de.polocloud.api.player.ICloudPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultPlayerEvent implements ICloudEvent {

    private final ICloudPlayer player;

    public DefaultPlayerEvent(final @NotNull ICloudPlayer player) {
        this.player = player;
    }

    public ICloudPlayer getPlayer() {
        return this.player;
    }

}
