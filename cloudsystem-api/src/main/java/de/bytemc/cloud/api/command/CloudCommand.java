package de.bytemc.cloud.api.command;

import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class CloudCommand {

    private String commandName, description;
    private String[] alias;
    private ExecutorType executorType;

    public CloudCommand(String commandName, String description, ExecutorType type, String... alias) {
        this.commandName = commandName;
        this.description = description;
        this.executorType = type;
        this.alias = alias;
    }

    public abstract void execute(ICommandSender sender, String[] args);

}
