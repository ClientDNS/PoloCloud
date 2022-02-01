package de.bytemc.cloud.wrapper;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.command.executor.ExecutorType;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.json.Document;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.wrapper.groups.GroupManager;
import de.bytemc.cloud.wrapper.network.WrapperClient;
import de.bytemc.cloud.wrapper.player.CloudPlayerManager;
import de.bytemc.cloud.wrapper.service.ServiceManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wrapper extends CloudAPI {

    public static void main(String[] args) {
        try {
            new Wrapper();

            final List<String> arguments = new ArrayList<>(Arrays.asList(args));
            final Class<?> main = Class.forName(arguments.remove(0));
            final Method method = main.getMethod("main", String[].class);
            final Thread thread = new Thread(() -> {
                try {
                    method.invoke(null, (Object) arguments.toArray(new String[0]));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }, "Minecraft-Thread");
            thread.setContextClassLoader(ClassLoader.getSystemClassLoader());
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Wrapper instance;

    private final IGroupManager groupManager;
    private final IServiceManager serviceManager;
    private final ICloudPlayerManager cloudPlayerManager;
    private final WrapperClient client;
    private final ICommandSender commandSender;

    public Wrapper() {
        super(CloudAPITypes.SERVICE);

        instance = this;

        final var property = new Document(new File("property.json")).get(PropertyFile.class);

        this.groupManager = new GroupManager();
        this.serviceManager = new ServiceManager(property);
        this.cloudPlayerManager = new CloudPlayerManager();
        this.client = new WrapperClient(property.getService(), property.getHostname(), property.getPort());
        this.commandSender = new ICommandSender() {
            @Override
            public void sendMessage(@NotNull String text) {
                System.out.println(text);
            }

            @Override
            public ExecutorType getCommandType() {
                return ExecutorType.INGAME;
            }
        };

        CloudAPI.getInstance().getLoggerProvider().logMessage("Successfully started plugin client.", LogType.SUCCESS);
    }

    public static Wrapper getInstance() {
        return instance;
    }

    @Override
    public ICommandSender getCommandSender() {
        return this.commandSender;
    }

    @Override
    public @NotNull IGroupManager getGroupManager() {
        return this.groupManager;
    }

    @Override
    public @NotNull IServiceManager getServiceManager() {
        return this.serviceManager;
    }

    @Override
    public @NotNull ICloudPlayerManager getCloudPlayerManager() {
        return this.cloudPlayerManager;
    }

    public WrapperClient getClient() {
        return this.client;
    }

}
