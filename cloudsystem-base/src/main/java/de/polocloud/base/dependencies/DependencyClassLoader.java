package de.polocloud.base.dependencies;

import java.net.URL;
import java.net.URLClassLoader;

public final class DependencyClassLoader extends URLClassLoader {

    public DependencyClassLoader(final URL[] urls, final ClassLoader classLoader) {
        super(urls, classLoader);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

}
