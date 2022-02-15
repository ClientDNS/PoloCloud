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

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public enum GameServerVersion {

    BUNGEE("https://ci.md-5.net/job/BungeeCord/lastBuild/artifact/bootstrap/target/BungeeCord.jar",
        "BungeeCord", "latest", ServiceTypes.PROXY),
    WATERFALL("waterfall", "latest", ServiceTypes.PROXY),
    PAPER_1_18_1("paper", "1.18.1", ServiceTypes.SERVER),
    PAPER_1_17_1("paper", "1.17.1", ServiceTypes.SERVER),
    PAPER_1_16_5("paper", "1.16.5", ServiceTypes.SERVER),
    PAPER_1_15_2("paper", "1.15.2", ServiceTypes.SERVER),
    PAPER_1_14_4("paper", "1.14.4", ServiceTypes.SERVER),
    PAPER_1_13_2("paper", "1.13.2", ServiceTypes.SERVER),
    PAPER_1_12_2("paper", "1.12.2", ServiceTypes.SERVER),
    PAPER_1_11_2("paper", "1.11.2", ServiceTypes.SERVER),
    PAPER_1_10_2("paper", "1.10.2", ServiceTypes.SERVER),
    PAPER_1_9_4("paper", "1.17.1", ServiceTypes.SERVER),
    PAPER_1_8_8("paper", "1.8.8", ServiceTypes.SERVER);

    private final String url;
    private final String title;
    private final String version;
    private final ServiceTypes serviceTypes;

    GameServerVersion(final String url, final String title, final String version, final ServiceTypes serviceTypes) {
        this.url = url;
        this.title = title;
        this.version = version;
        this.serviceTypes = serviceTypes;
    }

    GameServerVersion(final String title, final String version, final ServiceTypes serviceTypes) {
        final int build = this.getBuildNumber(title, version);
        String paperVersion = version;
        if (paperVersion.equals("latest")) {
            paperVersion = this.getLatestVersion(title);
        }
        this.url =
            "https://papermc.io/api/v2/projects/" + title + "/versions/" + paperVersion + "/builds/" + build
                + "/downloads/" + title + "-" + paperVersion + "-" + build + ".jar";
        this.title = title;
        this.version = version;
        this.serviceTypes = serviceTypes;
    }

    public boolean isProxy() {
        return this.serviceTypes == ServiceTypes.PROXY;
    }

    public String getJar() {
        return this.title + (!Objects.equals(this.version, "latest") ? "-" + this.version : "") + ".jar";
    }

    public static GameServerVersion getVersionByTitle(final String value) {
        return Arrays.stream(values()).filter(it -> (it.getTitle() +
            (!Objects.equals(it.getVersion(), "latest") ? "-" + it.getVersion() : "")).equalsIgnoreCase(value)).findAny().orElse(null);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void download() {
        final var file = new File("storage/jars", this.getJar());

        if (file.exists()) return;

        CloudAPI.getInstance().getLoggerProvider().logMessage("§7Downloading §bVersion§7... (§3" + this.getTitle() + "§7)");

        file.getParentFile().mkdirs();
        try {
            var url = this.getUrl();
            CloudAPI.getInstance().getLoggerProvider().logMessage(url); // debug
            FileUtils.copyURLToFile(new URL(url), file);

            if (this.title.equals("paper")) {
                final Process process = new ProcessBuilder("java", "-Dpaperclip.patchonly=true -jar", this.getJar())
                    .directory(file.getParentFile()).start();
                final InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                process.waitFor();
                process.destroyForcibly();
                bufferedReader.close();
                inputStreamReader.close();
                FileUtils.copyFile(new File("storage/jars/cache/patched_" + this.version + ".jar"), file);
                FileUtils.deleteDirectory(new File("storage/jars/cache"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            CloudAPI.getInstance().getLoggerProvider().logMessage("§cFailed to download version§7... (§3" + this.getTitle() + "§7)", LogType.ERROR);
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        String paperVersion = version;
        if (paperVersion.equals("latest")) {
            paperVersion = this.getLatestVersion(title);
        }
        final Document document = this.paperApiRequest("https://papermc.io/api/v2/projects/" + title + "/versions/" + paperVersion + "/");
        if (document != null) {
            final List<Integer> buildNumbers = document.get("builds", TypeToken.getParameterized(List.class, Integer.class).getType());
            return buildNumbers.get(buildNumbers.size() - 1);
        } else {
            return -1;
        }
    }

    private String getLatestVersion(final @NotNull String title) {
        final Document document = this.paperApiRequest("https://papermc.io/api/v2/projects/" + title);
        if (document != null) {
            final List<String> versions = document.get("versions", TypeToken.getParameterized(List.class, String.class).getType());
            return versions.get(versions.size() - 1);
        } else {
            return "Unknown";
        }
    }

    private Document paperApiRequest(final @NotNull String urlString) {
        try {
            final var url = new URL(urlString);
            final InputStream inputStream = url.openStream();
            final InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            final Document document = new Document(inputStreamReader);
            inputStreamReader.close();
            inputStream.close();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

