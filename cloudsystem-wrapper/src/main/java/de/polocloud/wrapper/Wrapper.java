package de.polocloud.wrapper;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.CloudAPIType;
import de.polocloud.api.groups.GroupManager;
import de.polocloud.api.json.Document;
import de.polocloud.api.logger.Logger;
import de.polocloud.api.network.packet.init.CacheInitPacket;
import de.polocloud.api.player.PlayerManager;
import de.polocloud.api.service.CloudService;
import de.polocloud.api.service.ServiceManager;
import de.polocloud.wrapper.group.WrapperGroupManager;
import de.polocloud.wrapper.logger.WrapperLogger;
import de.polocloud.wrapper.network.WrapperClient;
import de.polocloud.wrapper.player.CloudPlayerManager;
import de.polocloud.wrapper.service.WrapperServiceManager;
import de.polocloud.wrapper.transformer.ClassTransformer;
import de.polocloud.wrapper.transformer.Transformer;
import de.polocloud.wrapper.transformer.bukkit.BukkitCommodoreTransformer;
import de.polocloud.wrapper.transformer.bukkit.BukkitMainTransformer;
import de.polocloud.wrapper.transformer.bukkit.PaperConfigTransformer;
import de.polocloud.wrapper.transformer.netty.NettyEpollTransformer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
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
            final var wrapper = new Wrapper();

            var cacheInitialized = new AtomicBoolean(false);
            wrapper.getPacketHandler().registerPacketListener(CacheInitPacket.class, (channelHandlerContext, packet) -> cacheInitialized.set(true));

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
                            final var className = jarEntry.getName()
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
            }, "PoloCloud-Service-Thread");
            thread.setContextClassLoader(classLoader);
            if (cacheInitialized.get()) {
                thread.start();
            } else {
                wrapper.getPacketHandler().registerPacketListener(CacheInitPacket.class,
                    (channelHandlerContext, packet) -> thread.start());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Wrapper instance;

    private final GroupManager groupManager;
    private final ServiceManager serviceManager;
    private final PlayerManager playerManager;
    private final WrapperClient client;

    public Wrapper() {
        super(CloudAPIType.SERVICE);

        instance = this;

        this.addTransformer("org/bukkit/craftbukkit", "Main", new BukkitMainTransformer());
        this.addTransformer("org/bukkit/craftbukkit", "Commodore", new BukkitCommodoreTransformer());
        this.addTransformer("org/github/paperspigot", "PaperSpigotConfig", new PaperConfigTransformer());
        this.addTransformer(name -> name.endsWith("Epoll") && name.startsWith("io") && name.contains("netty/channel/epoll/"), new NettyEpollTransformer());

        final var property = new Document(new File("property.json")).get(PropertyFile.class);

        this.logger = new WrapperLogger();
        this.groupManager = new WrapperGroupManager();
        this.serviceManager = new WrapperServiceManager(property);
        this.playerManager = new CloudPlayerManager();
        this.client = new WrapperClient(this.packetHandler, property.getService(), property.getHostname(), property.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "PoloCloud-Shutdown-Thread"));
    }

    public static Wrapper getInstance() {
        return instance;
    }

    private void stop() {
        this.client.close();
    }

    private void addTransformer(final @NotNull Predicate<String> predicate, final @NotNull Transformer transformer) {
        instrumentation.addTransformer(new ClassTransformer(transformer, predicate, instrumentation));
    }

    private void addTransformer(final @NotNull String packagePrefix, final @NotNull String className, final @NotNull Transformer transformer) {
        this.addTransformer(name -> {
            if (!name.startsWith(packagePrefix)) return false;
            final var lastSlash = name.lastIndexOf('/');
            if (lastSlash != -1 && name.length() > lastSlash) {
                var simpleName = name.substring(lastSlash + 1);
                return className.equals(simpleName);
            }
            return false;
        }, transformer);
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public @NotNull GroupManager getGroupManager() {
        return this.groupManager;
    }

    @Override
    public @NotNull ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    @Override
    public @NotNull PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public CloudService thisService() {
        return ((WrapperServiceManager) this.serviceManager).thisService();
    }

    public WrapperClient getClient() {
        return this.client;
    }

}
