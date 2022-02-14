package de.bytemc.cloud.api.command;

import de.bytemc.cloud.api.CloudAPI;
import lombok.Getter;

@Getter
public abstract class CloudCommand {

    private final String commandName, description;
    private final String[] alias;

    public CloudCommand(String commandName, String description, String... alias) {
        this.commandName = commandName;
        this.description = description;
        this.alias = alias;
    }

    public abstract void execute(CloudAPI cloudAPI, String[] args);

}
