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
            final var file = new File("tmp/base.jar");
            file.getParentFile().mkdir();
            Files.copy(Objects.requireNonNull(ClassLoader.getSystemClassLoader()
                .getResourceAsStream("cloudsystem-base.jar")), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            final var classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, ClassLoader.getSystemClassLoader()) {
                @Override
                public void addURL(URL url) {
                    super.addURL(url);
                }
            };
            classLoader.loadClass("de.polocloud.base.Base").getConstructor().newInstance();
        } catch (IOException | ClassNotFoundException | InvocationTargetException
            | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
