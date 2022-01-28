package de.bytemc.cloud.api.versions;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.utils.ServiceTypes;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum GameServerVersion {

    BUNGEE("https://ci.md-5.net/job/BungeeCord/lastBuild/artifact/bootstrap/target/BungeeCord.jar", "BungeeCord", ServiceTypes.PROXY),
    PAPERSPIGOT_1_17_1("https://papermc.io/api/v2/projects/paper/versions/1.17.1/builds/408/downloads/paper-1.17.1-408.jar","Paperspigot-1.17.1", ServiceTypes.SERVER);

    private String url;
    private String title;
    private ServiceTypes serviceTypes;

    public boolean isProxy(){
        return serviceTypes == ServiceTypes.PROXY;
    }

    public String getJar(){
        return this.title + ".jar";
    }

    public static GameServerVersion getVersionByTitle(String value){
        return Arrays.stream(values()).filter(it -> it.getTitle().equalsIgnoreCase(value)).findAny().orElse(null);
    }

    public void download() {
        var file = new File("storage/jars");
        if(!file.exists()) file.mkdirs();

        if(isDownloaded()) return;

        CloudAPI.getInstance().getLoggerProvider().logMessage( "§7Downloading §bVersion§7... (§3" + this.getTitle() + "§7)");

        var downloadedVersion = new File("storage/jars", this.getJar());
        downloadedVersion.getParentFile().mkdirs();
        try {
            FileUtils.copyURLToFile(new URL(this.getUrl()), downloadedVersion);
        } catch (IOException e) {
            e.printStackTrace();
            CloudAPI.getInstance().getLoggerProvider().logMessage("§cFailed to download version§7... (§3" + this.getTitle() + "§7)", LogType.ERROR);
            return;
        }
        CloudAPI.getInstance().getLoggerProvider().logMessage("Downloading of (§3" + this.getTitle() + "§7)§a successfully §7completed.");
    }

    private boolean isDownloaded(){
        return new File("storage/jars", this.getJar()).exists();
    }

    @SneakyThrows
    public void copy(IService service){
        FileUtils.copyFile(new File("storage/jars", this.getJar()), new File("live/" + service.getName() + "/" +  this.getJar()));
    }

}

