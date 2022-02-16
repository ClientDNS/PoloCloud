package de.polocloud.base.command;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface CommandManager {

    /**
     * registers a command
     * @param command to register
     */
    void registerCommand(@NotNull CloudCommand command);

    /**
     * registers commands
     * @param commands to register
     */
    void registerCommands(@NotNull CloudCommand... commands);

    /**
     * unregisters a command
     * @param command to unregister
     */
    void unregisterCommand(@NotNull CloudCommand command);

    /**
     * executes a command
     * @param command the command to execute
     */
    void execute(@NotNull String command);

    /**
     * gets all cached commands
     * @return the cached commands
     */
    @NotNull Map<String, CloudCommand> getCachedCloudCommands();

}
