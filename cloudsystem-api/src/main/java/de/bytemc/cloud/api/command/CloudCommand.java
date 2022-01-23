package de.bytemc.cloud.api.command;

import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public abstract class CloudCommand {

    private String commandName, description;
    private ExecutorType executorType;

    public abstract void execute(ICommandSender sender, String[] args);

}
