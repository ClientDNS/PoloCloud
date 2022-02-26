package de.polocloud.base.command;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.CloudAPIType;
import de.polocloud.base.Base;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class SimpleCommandManager implements CommandManager {

    public final Map<String, CloudCommand> cachedCloudCommands;

    public SimpleCommandManager() {
        this.cachedCloudCommands = new HashMap<>();
    }

    public void execute(final @NotNull String command) {
        final var args = new ArrayList<>(Arrays.asList(command.split(" ")));
        if (CloudAPI.getInstance().getCloudAPITypes().equals(CloudAPIType.NODE)) {
            final var cloudCommand = this.cachedCloudCommands.get(args.get(0));
            if (cloudCommand == null) return;
            args.remove(0);
            cloudCommand.execute(Base.getInstance(), args.toArray(new String[]{}));
        }
    }

    @Override
    public void registerCommand(final @NotNull CloudCommand command) {
        this.cachedCloudCommands.put(command.getName(), command);
        for (final var alias : command.getAliases()) this.cachedCloudCommands.put(alias, command);
    }

    @Override
    public void registerCommands(@NotNull CloudCommand... commands) {
        for (final var command : commands) this.registerCommand(command);
    }

    @Override
    public void unregisterCommand(final @NotNull CloudCommand command) {
        this.cachedCloudCommands.forEach((s, cloudCommand) -> {
            if (cloudCommand == command) this.cachedCloudCommands.remove(s);
        });
    }

}
