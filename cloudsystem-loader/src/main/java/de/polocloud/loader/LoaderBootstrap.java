package de.polocloud.loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public final class LoaderBootstrap {

    public static void main(String[] args) {
        try {
            var path = new File("storage/jars/base.jar").toPath();
            path.toFile().getParentFile().mkdir();
            Files.copy(Objects.requireNonNull(ClassLoader.getSystemClassLoader()
                .getResourceAsStream("cloudsystem-base.jar")), path, StandardCopyOption.REPLACE_EXISTING);
            final var classLoader = new URLClassLoader(new URL[]{path.toUri().toURL()}, ClassLoader.getSystemClassLoader()) {
                @Override
                public void addURL(URL url) {
                    super.addURL(url);
                }
            };
            Thread.currentThread().setContextClassLoader(classLoader);
            classLoader.loadClass("de.polocloud.base.Base").getConstructor().newInstance();
        } catch (IOException | ClassNotFoundException | InvocationTargetException
            | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
