package de.bytemc.cloud.wrapper;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import de.bytemc.cloud.api.groups.IGroupManager;
import de.bytemc.cloud.api.json.Document;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.LoggerProvider;
import de.bytemc.cloud.api.player.ICloudPlayerManager;
import de.bytemc.cloud.api.services.IService;
import de.bytemc.cloud.api.services.IServiceManager;
import de.bytemc.cloud.wrapper.groups.GroupManager;
import de.bytemc.cloud.wrapper.logger.WrapperLoggerProvider;
import de.bytemc.cloud.wrapper.network.WrapperClient;
import de.bytemc.cloud.wrapper.player.CloudPlayerManager;
import de.bytemc.cloud.wrapper.service.ServiceManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public final class Wrapper extends CloudAPI {

    private static Instrumentation instrumentation;

    public static void premain(final String s, final Instrumentation instrumentation) {
        Wrapper.instrumentation = instrumentation;
    }

    public static void main(String[] args) {
        try {
            new Wrapper();

            final var arguments = new ArrayList<>(Arrays.asList(args));
            final var main = arguments.remove(0);
            final var applicationFile = Paths.get(arguments.remove(0));

            var classLoader = ClassLoader.getSystemClassLoader();
            if (Boolean.parseBoolean(arguments.remove(0))) {
                classLoader = new URLClassLoader(new URL[]{applicationFile.toUri().toURL()}, ClassLoader.getSystemClassLoader());

                try (final var jarInputStream = new JarInputStream(Files.newInputStream(applicationFile))) {
                    JarEntry jarEntry;
                    while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                        if (jarEntry.getName().endsWith(".class")) {
                            final String className = jarEntry.getName()
                                .replace('/', '.').replace(".class", "");
                            Class.forName(className, false, classLoader);
                        }
                    }
                }
            }

            instrumentation.appendToSystemClassLoaderSearch(new JarFile(applicationFile.toFile()));

            final var mainClass = Class.forName(main, true, classLoader);
            final var method = mainClass.getMethod("main", String[].class);
            final var thread = new Thread(() -> {
                try {
                    method.invoke(null, (Object) arguments.toArray(new String[0]));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }, "Minecraft-Thread");
            thread.setContextClassLoader(classLoader);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Wrapper instance;

    private final LoggerProvider loggerProvider;
    private final IGroupManager groupManager;
    private final IServiceManager serviceManager;
    private final ICloudPlayerManager cloudPlayerManager;
    private final WrapperClient client;
    private final ICommandSender commandSender;

    public Wrapper() {
        super(CloudAPITypes.SERVICE);

        instance = this;

        final var property = new Document(new File("property.json")).get(PropertyFile.class);

        this.loggerProvider = new WrapperLoggerProvider();
        this.groupManager = new GroupManager();
        this.serviceManager = new ServiceManager(property);
        this.cloudPlayerManager = new CloudPlayerManager();
        this.client = new WrapperClient(property.getService(), property.getHostname(), property.getPort());
        this.commandSender = text -> System.out.println(text);

        this.loggerProvider.logMessage("Successfully started plugin client.", LogType.SUCCESS);
    }

    public static Wrapper getInstance() {
        return instance;
    }

    @Override
    public LoggerProvider getLoggerProvider() {
        return this.loggerProvider;
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

    public IService thisService() {
        return ((ServiceManager) serviceManager).thisService();
    }

    public WrapperClient getClient() {
        return this.client;
    }

}
