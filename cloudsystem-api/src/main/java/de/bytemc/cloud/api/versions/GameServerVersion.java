package de.bytemc.cloud.api.versions;

import com.google.gson.reflect.TypeToken;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.groups.utils.ServiceTypes;
import de.bytemc.cloud.api.json.Document;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.services.IService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public enum GameServerVersion {

    // TODO fix paperclip
    BUNGEE("https://ci.md-5.net/job/BungeeCord/lastBuild/artifact/bootstrap/target/BungeeCord.jar",
        "BungeeCord", "latest", ServiceTypes.PROXY),
    WATERFALL("https://papermc.io/api/v2/projects/%title%/versions/%version%/builds/%build%/downloads/waterfall-%version%-%build%.jar",
        "waterfall", "1.18", ServiceTypes.PROXY),
    PAPER_1_18_1("https://papermc.io/api/v2/projects/%title%/versions/%version%/builds/%build%/downloads/paper-%version%-%build%.jar",
        "paper", "1.18.1", ServiceTypes.SERVER),
    PAPER_1_17_1("https://papermc.io/api/v2/projects/%title%/versions/%version%/builds/%build%/downloads/paper-%version%-%build%.jar",
        "paper", "1.17.1", ServiceTypes.SERVER);

    private final String url;
    private final String title;
    private final String version;
    private final ServiceTypes serviceTypes;

    GameServerVersion(final String url, final String title, final String version, final ServiceTypes serviceTypes) {
        this.url = url.replaceAll("%title%", title).replaceAll("%version%", version);
        this.title = title;
        this.version = version;
        this.serviceTypes = serviceTypes;
    }

    public boolean isProxy() {
        return this.serviceTypes == ServiceTypes.PROXY;
    }

    public String getJar() {
        return this.title + (!Objects.equals(this.version, "latest") ? "-"  + this.version : "") + ".jar";
    }

    public static GameServerVersion getVersionByTitle(final String value) {
        return Arrays.stream(values()).filter(it -> (it.getTitle()  + "-" + it.getVersion()).equalsIgnoreCase(value)).findAny().orElse(null);
    }

    public void download() {
        if (this.isDownloaded()) return;

        CloudAPI.getInstance().getLoggerProvider().logMessage("§7Downloading §bVersion§7... (§3" + this.getTitle() + "§7)");

        final var downloadedVersion = new File("storage/jars", this.getJar());
        downloadedVersion.getParentFile().mkdirs();
        try {
            var url = this.getUrl();
            if (url.contains("%build%")) {
                final int buildNumber = this.getBuildNumber(this.title, this.version);
                url = url.replaceAll("%build%", String.valueOf(buildNumber));
            }
            CloudAPI.getInstance().getLoggerProvider().logMessage(url); // debug
            FileUtils.copyURLToFile(new URL(url), downloadedVersion);
        } catch (IOException e) {
            e.printStackTrace();
            CloudAPI.getInstance().getLoggerProvider().logMessage("§cFailed to download version§7... (§3" + this.getTitle() + "§7)", LogType.ERROR);
            return;
        }
        CloudAPI.getInstance().getLoggerProvider().logMessage("Downloading of (§3" + this.getTitle() + "§7)§a successfully §7completed.");
    }

    private boolean isDownloaded() {
        return new File("storage/jars", this.getJar()).exists();
    }

    @SneakyThrows
    public void copy(final @NotNull IService service) {
        FileUtils.copyFile(new File("storage/jars", this.getJar()), new File("live/" + service.getName() + "/" + this.getJar()));
    }

    private int getBuildNumber(final @NotNull String title, final @NotNull String version) {
        try {
            final var url = new URL("https://papermc.io/api/v2/projects/" + title + "/versions/" + version + "/");
            final var inputStreamReader = new InputStreamReader(url.openStream());
            final List<Integer> buildNumbers = new Document(inputStreamReader)
                .get("builds", TypeToken.getParameterized(List.class, Integer.class).getType());
            inputStreamReader.close();
            return buildNumbers.get(buildNumbers.size() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

}

