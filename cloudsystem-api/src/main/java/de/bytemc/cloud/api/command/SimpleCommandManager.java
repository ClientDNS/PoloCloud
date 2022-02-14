package de.bytemc.cloud.api.command;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Getter
public class SimpleCommandManager implements CommandManager {

    public final List<CloudCommand> cachedCloudCommands;

    public SimpleCommandManager() {
        this.cachedCloudCommands = Lists.newArrayList();
    }

    public void execute(final @NotNull String command){
        List<String> args = Lists.newArrayList(command.split(" "));
        if(CloudAPI.getInstance().getCloudAPITypes().equals(CloudAPITypes.NODE)) {
           CloudCommand cloudCommand = cachedCloudCommands.stream()
               .filter(it -> it.getCommandName().equalsIgnoreCase(args.get(0)) || Arrays.stream(it.getAlias()).anyMatch(s -> s.equalsIgnoreCase(args.get(0))))
               .findFirst()
               .orElse(null);
            if (cloudCommand == null) return;
            args.remove(0);
            cloudCommand.execute(CloudAPI.getInstance().getCommandSender(), args.toArray(new String[]{}));
        }
    }


    @Override
    public void registerCommand(final @NotNull CloudCommand command) {
        this.cachedCloudCommands.add(command);
    }

    @Override
    public void registerCommands(@NotNull CloudCommand... commands) {
        for (final CloudCommand command : commands) this.registerCommand(command);
    }

    @Override
    public void unregisterCommand(final @NotNull CloudCommand command) {
        this.cachedCloudCommands.remove(command);
    }

}
