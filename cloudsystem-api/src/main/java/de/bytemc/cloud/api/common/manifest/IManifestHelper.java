package de.bytemc.cloud.api.common.manifest;

public interface IManifestHelper {

    String MANIFEST_LOCATION = "META-INF/MANIFEST.MF";

    String getValueOfManifestEntry(String key);

    String getValueOfManifestEntryOrDefault(String key, String defaultValue);

    void reloadManifest();

    void reloadManifest(Class<?> clazs);

}
