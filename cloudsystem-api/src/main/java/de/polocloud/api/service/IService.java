package de.polocloud.api.service;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.groups.IServiceGroup;
import de.polocloud.api.service.utils.ServiceState;
import de.polocloud.api.service.utils.ServiceVisibility;
import de.polocloud.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface IService {

    /**
     * @return the name of the service
     */
    @NotNull String getName();

    /**
     * @return the service id
     */
    int getServiceId();

    /**
     * @return the port of the service
     */
    int getPort();

    /**
     * @return the host name of the service
     */
    @NotNull String getHostName();

    /**
     * @return the node on which the service is started
     */
    @NotNull String getNode();

    /**
     * @return the group of the service
     */
    @NotNull IServiceGroup getGroup();

    /**
     * @return the group of the service
     */
    @Deprecated(forRemoval = true)
    default @NotNull IServiceGroup getServiceGroup() {
        return this.getGroup();
    }

    /**
     * sets the service state
     *
     * @param serviceState the state to set
     */
    void setServiceState(@NotNull ServiceState serviceState);

    /**
     * @return the state of the service
     */
    @NotNull ServiceState getServiceState();

    /**
     * @return the max players of the service
     */
    int getMaxPlayers();

    /**
     * sets the max players of the service
     * @param slots the amount to set
     */
    void setMaxPlayers(int slots);

    /**
     * @return the service visibility of the service
     */
    @NotNull ServiceVisibility getServiceVisibility();

    /**
     * sets the service visibility
     * @param serviceVisibility the service visibility to set
     */
    void setServiceVisibility(@NotNull ServiceVisibility serviceVisibility);

    /**
     * @return the online amount of the service
     */
    default int getOnlineCount() {
        return (int) CloudAPI.getInstance().getPlayerManager().getPlayers()
            .stream()
            .filter(player -> {
                IService service = this.getGroup().getGameServerVersion().isProxy() ? player.getProxyServer() : player.getServer();
                return service != null && service.equals(this);
            }).count();
    }

    /**
     * @return if the service is full
     */
    default boolean isFull() {
        return this.getOnlineCount() >= this.getMaxPlayers();
    }

    /**
     * edits the properties of the service and update then
     * @param serviceConsumer the consumer to change the properties
     */
    void edit(@NotNull Consumer<IService> serviceConsumer);

    /**
     * @return the motd of the service
     */
    @NotNull String getMotd();

    /**
     * sets the motd of the service
     * @param motd the motd to set
     */
    void setMotd(@NotNull String motd);

    /**
     * sends a packet to a service
     * @param packet the packet to send
     */
    void sendPacket(@NotNull Packet packet);

    /**
     * executes a command on the service
     */
    void executeCommand(@NotNull String command);

    /**
     * stops the service
     */
    void stop();

    /**
     * updates the properties of the service
     */
    void update();

}
