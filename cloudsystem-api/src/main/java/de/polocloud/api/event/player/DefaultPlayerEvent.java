package de.polocloud.api.event.player;

import de.polocloud.api.event.ICloudEvent;
import de.polocloud.api.player.CloudPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultPlayerEvent implements ICloudEvent {

    private final CloudPlayer player;

    public DefaultPlayerEvent(final @NotNull CloudPlayer player) {
        this.player = player;
    }

    public CloudPlayer getPlayer() {
        return this.player;
    }

}
