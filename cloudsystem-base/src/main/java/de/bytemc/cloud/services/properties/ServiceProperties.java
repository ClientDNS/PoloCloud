package de.bytemc.cloud.services.properties;

import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Getter
public abstract class ServiceProperties {

    private File file;
    private int port;
    private String[] properties;

    private final boolean can;

    public ServiceProperties(File file, String child, int port) {
        can = new File(file.getPath(), child).exists();
        this.file = new File(file.getPath(), child);

        this.port = port;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public void writeFile() {
        if (!can) {
            try {
                FileWriter fileWriter = new FileWriter(file);

                for (String line : properties) {
                    fileWriter.write(line + "\n");
                }
                fileWriter.close();

            } catch (IOException ignored) { }
        }
    }
}
