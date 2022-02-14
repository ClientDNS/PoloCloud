package de.bytemc.cloud.api.common.manifest.impl;

import de.bytemc.cloud.api.common.manifest.IManifestHelper;

import java.io.IOException;
import java.util.jar.Manifest;

public class SimpleManifestHelper implements IManifestHelper {

    private Manifest loadedManifest;
    private Class<?> parentClass;

    public SimpleManifestHelper(Class<?> clazs) {
        try {
            this.parentClass = clazs;
            this.loadedManifest = new Manifest(clazs
                    .getClassLoader()
                    .getResourceAsStream(MANIFEST_LOCATION));
        } catch (IOException ignored) {
            loadedManifest = null;
        }
    }

    @Override
    public String getValueOfManifestEntry(String key) {
        return getValueOfManifestEntryOrDefault(key, "ERR");

    }

    @Override
    public String getValueOfManifestEntryOrDefault(String key, String defaultValue) {
        if (loadedManifest == null) {
            return defaultValue;
        }
        return loadedManifest.getMainAttributes().getValue(key);
    }


    @Override
    public void reloadManifest(Class<?> clazs) {
        try {
            this.parentClass = clazs;
            this.loadedManifest = new Manifest(clazs
                    .getClassLoader()
                    .getResourceAsStream(MANIFEST_LOCATION));
        } catch (IOException ignored) {
        }
    }

    @Override
    public void reloadManifest() {
        reloadManifest(this.parentClass);
    }
}
