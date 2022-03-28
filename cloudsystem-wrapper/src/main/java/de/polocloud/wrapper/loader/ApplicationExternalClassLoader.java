package de.polocloud.wrapper.loader;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

@Getter
public class ApplicationExternalClassLoader extends URLClassLoader {

    private boolean closed = false;

    @SneakyThrows
    public ApplicationExternalClassLoader() {
        super(new URL[]{}, ClassLoader.getSystemClassLoader());
    }

    @SneakyThrows
    public ApplicationExternalClassLoader addUrl(@NotNull final Path url){
        this.addURL(url.toUri().toURL());
        return this;
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        super.close();
    }
}
