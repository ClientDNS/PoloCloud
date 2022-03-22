package de.polocloud.base.dependencies;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.Map;

public final class DependencyHandler {

    private final File librariesDirectory;
    private final Map<Dependency, File> loadedDependencies;

    public DependencyHandler() {
        this.librariesDirectory = new File("libraries");
        try {
            if (!this.librariesDirectory.exists()) {
                Files.createDirectory(this.librariesDirectory.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.loadedDependencies = new EnumMap<>(Dependency.class);
    }

    public void loadDependencies(final Dependency... dependencies) {
        for (final var dependency : dependencies) this.loadDependency(dependency);
    }

    public void loadDependency(final Dependency dependency) {
        final var file = new File(this.librariesDirectory, dependency.getFileName());

        if (!file.exists()) {
            try {
                final var urlConnection = new URL("https://repo1.maven.org/maven2/" + dependency.getMavenRepoPath())
                    .openConnection();
                try (final var inputStream = urlConnection.getInputStream()) {
                    Files.write(file.toPath(), inputStream.readAllBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            final var method = this.getClass().getClassLoader().getClass().getMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(this.getClass().getClassLoader(), file.toURI().toURL());
        } catch (NoSuchMethodException | MalformedURLException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.loadedDependencies.put(dependency, file);
    }

}
