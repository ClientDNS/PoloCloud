package de.bytemc.cloud.api.command;

import com.google.common.collect.Lists;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import lombok.Getter;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Getter
public class SimpleCommandManager implements CommandManager {

    public final List<CloudCommand> cachedCloudCommands;

    public SimpleCommandManager() {
        this.cachedCloudCommands = Lists.newArrayList();
    }

    public boolean execute(String command){
        List<String> args = Lists.newArrayList(command.split(" "));
        if(CloudAPI.getInstance().getCloudAPITypes().equals(CloudAPITypes.NODE)) {
           CloudCommand cloudCommand = cachedCloudCommands.stream()
               .filter(it -> it.getCommandName().equalsIgnoreCase(args.get(0)) || Arrays.stream(it.getAlias()).anyMatch(s -> s.equalsIgnoreCase(args.get(0))))
               .findFirst()
               .orElse(null);
            if (cloudCommand == null) return false;
            args.remove(0);
            cloudCommand.execute(CloudAPI.getInstance().getCommandSender(), args.toArray(new String[]{}));
            return true;
        }
        return false;
    }


    @Override
    public void registerCommand(CloudCommand command) {
        this.cachedCloudCommands.add(command);
    }

    @Override
    public void unregisterCommand(CloudCommand command) {
        this.cachedCloudCommands.remove(command);
    }

    public void registerCommandByPackage(String packageInput){
        Reflections reflections = new Reflections(packageInput);

        reflections.getSubTypesOf(CloudCommand.class).forEach(it -> {
            try {
                registerCommand(it.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

}
