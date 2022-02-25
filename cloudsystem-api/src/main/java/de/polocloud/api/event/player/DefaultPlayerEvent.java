package de.polocloud.api.event.player;

import de.polocloud.api.event.CloudEvent;
import de.polocloud.api.player.CloudPlayer;
import org.jetbrains.annotations.NotNull;

public abstract class DefaultPlayerEvent implements CloudEvent {

    private final CloudPlayer player;

    public DefaultPlayerEvent(final @NotNull CloudPlayer player) {
        this.player = player;
    }

    public CloudPlayer getPlayer() {
        return this.player;
    }

}
