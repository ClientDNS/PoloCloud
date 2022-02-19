package de.polocloud.base.command;

import de.polocloud.api.CloudAPI;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class CloudCommand {

    private final String name, description;
    private final String[] aliases;

    public CloudCommand(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(CloudAPI cloudAPI, String[] args);

    public List<String> tabComplete(final String[] arguments) {
        return null;
    }

}
