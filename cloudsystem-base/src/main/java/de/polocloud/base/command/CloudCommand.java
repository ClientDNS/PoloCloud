package de.polocloud.base.command;

import de.polocloud.base.Base;
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

    public abstract void execute(Base base, String[] args);

    public List<String> tabComplete(final String[] arguments) {
        return null;
    }

}
