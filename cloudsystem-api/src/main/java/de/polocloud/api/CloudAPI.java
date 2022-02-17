package de.polocloud.api;

import de.polocloud.api.event.EventHandler;
import de.polocloud.api.event.IEventHandler;
import de.polocloud.api.groups.IGroupManager;
import de.polocloud.api.logger.Logger;
import de.polocloud.api.network.packet.CustomPacket;
import de.polocloud.api.network.packet.QueryPacket;
import de.polocloud.api.network.packet.RedirectPacket;
import de.polocloud.api.network.packet.group.ServiceGroupCacheUpdatePacket;
import de.polocloud.api.network.packet.group.ServiceGroupExecutePacket;
import de.polocloud.api.network.packet.group.ServiceGroupUpdatePacket;
import de.polocloud.api.network.packet.init.CacheInitPacket;
import de.polocloud.api.network.packet.player.*;
import de.polocloud.api.network.packet.service.ServiceAddPacket;
import de.polocloud.api.network.packet.service.ServiceRemovePacket;
import de.polocloud.api.network.packet.service.ServiceRequestShutdownPacket;
import de.polocloud.api.network.packet.service.ServiceUpdatePacket;
import de.polocloud.api.player.IPlayerManager;
import de.polocloud.api.service.IServiceManager;
import de.polocloud.network.packet.PacketHandler;
import de.polocloud.network.packet.auth.NodeHandshakeAuthenticationPacket;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class CloudAPI {

    @Getter
    protected static CloudAPI instance;

    private final CloudAPIType cloudAPITypes;
    protected final PacketHandler packetHandler;
    protected final IEventHandler eventHandler;

    protected Logger logger;

    protected CloudAPI(final CloudAPIType cloudAPIType) {
        instance = this;

        this.cloudAPITypes = cloudAPIType;
        this.packetHandler = new PacketHandler(
            NodeHandshakeAuthenticationPacket.class, QueryPacket.class, RedirectPacket.class, CustomPacket.class,
            ServiceGroupCacheUpdatePacket.class, ServiceGroupExecutePacket.class, ServiceGroupUpdatePacket.class,
            CacheInitPacket.class, CloudPlayerDisconnectPacket.class, CloudPlayerKickPacket.class,
            CloudPlayerLoginPacket.class, CloudPlayerMessagePacket.class, CloudPlayerSendServicePacket.class,
            CloudPlayerUpdatePacket.class, ServiceAddPacket.class, ServiceRemovePacket.class,
            ServiceRequestShutdownPacket.class, ServiceUpdatePacket.class);
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

