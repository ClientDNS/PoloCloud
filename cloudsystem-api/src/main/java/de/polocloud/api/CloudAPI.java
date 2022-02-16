package de.polocloud.api;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.network.impl.NetworkHandler;
import de.polocloud.api.groups.IGroupManager;
import de.polocloud.api.logger.Logger;
import de.polocloud.api.network.INetworkHandler;
import de.polocloud.api.player.IPlayerManager;
import de.polocloud.api.service.IServiceManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class CloudAPI {

    @Getter
    protected static CloudAPI instance;

    private final CloudAPIType cloudAPITypes;
    protected final INetworkHandler networkHandler;
    protected final IEventHandler eventHandler;

    protected Logger logger;

    protected CloudAPI(final CloudAPIType cloudAPIType) {
        instance = this;

        this.cloudAPITypes = cloudAPIType;
        this.networkHandler = new NetworkHandler();
        this.eventHandler = new EventHandler();
    }

    /**
     * @return the logger provider
     */
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * @return the group manager
     */
    public abstract @NotNull IGroupManager getGroupManager();

    /**
     * @return the service manager
     */
    public abstract @NotNull IServiceManager getServiceManager();

    /**
     * @return the player manager
     */
    public abstract @NotNull IPlayerManager getPlayerManager();

}

