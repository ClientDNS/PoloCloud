package de.bytemc.cloud.api.command;


import java.util.List;

public interface CommandManager {

    void registerCommand(CloudCommand command);

    void unregisterCommand(CloudCommand command);

    boolean execute(String command);

    List<CloudCommand> getCachedCloudCommands();

    void registerCommandByPackage(String input);

}
