package de.polocloud.wrapper;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.CloudAPIType;
import de.polocloud.api.groups.IGroupManager;
import de.polocloud.api.json.Document;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.logger.Logger;
import de.polocloud.api.player.IPlayerManager;
import de.polocloud.api.service.IService;
import de.polocloud.api.service.IServiceManager;
import de.polocloud.wrapper.group.GroupManager;
import de.polocloud.wrapper.logger.WrapperLogger;
import de.polocloud.wrapper.network.WrapperClient;
import de.polocloud.wrapper.player.CloudPlayerManager;
import de.polocloud.wrapper.service.ServiceManager;
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

    private final IGroupManager groupManager;
    private final IServiceManager serviceManager;
    private final IPlayerManager playerManager;
    private final WrapperClient client;

    public Wrapper() {
        super(CloudAPIType.SERVICE);

        instance = this;

        final var property = new Document(new File("property.json")).get(PropertyFile.class);

        this.logger = new WrapperLogger();
        this.groupManager = new GroupManager();
        this.serviceManager = new ServiceManager(property);
        this.playerManager = new CloudPlayerManager();
        this.client = new WrapperClient(property.getService(), property.getHostname(), property.getPort());

        this.logger.log("Successfully started plugin client.", LogType.SUCCESS);
    }

    public static Wrapper getInstance() {
        return instance;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
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
    public @NotNull IPlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public IService thisService() {
        return ((ServiceManager) serviceManager).thisService();
    }

    public WrapperClient getClient() {
        return this.client;
    }

}
