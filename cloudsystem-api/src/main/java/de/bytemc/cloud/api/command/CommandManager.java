package de.bytemc.cloud.api.command;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CommandManager {

    /**
     * registers a command
     * @param command to register
     */
    void registerCommand(@NotNull CloudCommand command);

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
    @NotNull List<CloudCommand> getCachedCloudCommands();

    /**
     * registers all commands in a package
     * @param input the package to register all commands
     */
    void registerCommandByPackage(@NotNull String input);

}
