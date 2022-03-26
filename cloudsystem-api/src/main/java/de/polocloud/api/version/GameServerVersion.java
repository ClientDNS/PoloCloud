package de.polocloud.api.version;

import com.google.gson.reflect.TypeToken;
import de.polocloud.api.json.Document;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

@Getter
public final class GameServerVersion {

    public static Map<String, GameServerVersion> VERSIONS = new HashMap<>();

    public static GameServerVersion WATERFALL = new GameServerVersion("waterfall", "latest", true);
    public static GameServerVersion VELOCITY = new GameServerVersion("velocity", "latest", true);
    public static GameServerVersion PAPER_1_18_2 = new GameServerVersion("paper", "1.18.2");
    @Deprecated(since = "2.2.0")
    public static GameServerVersion PAPER_1_18_1 = new GameServerVersion("paper", "1.18.1");
    public static final GameServerVersion PAPER_1_17_1 = new GameServerVersion("paper", "1.17.1");
    public static final GameServerVersion PAPER_1_16_5 = new GameServerVersion("paper", "1.16.5");
    public static final GameServerVersion PAPER_1_15_2 = new GameServerVersion("paper", "1.15.2");
    public static final GameServerVersion PAPER_1_14_4 = new GameServerVersion("paper", "1.14.4");
    public static final GameServerVersion PAPER_1_13_2 = new GameServerVersion("paper", "1.13.2");
    public static final GameServerVersion PAPER_1_12_2 = new GameServerVersion("paper", "1.12.2");
    public static final GameServerVersion PAPER_1_11_2 = new GameServerVersion("paper", "1.11.2");
    public static final GameServerVersion PAPER_1_10_2 = new GameServerVersion("paper", "1.10.2");
    public static final GameServerVersion PAPER_1_9_4 = new GameServerVersion("paper", "1.9.4");
    public static final GameServerVersion PAPER_1_8_8 = new GameServerVersion("paper", "1.8.8");
    public static final GameServerVersion GLOWSTONE_1_12_2 = new GameServerVersion("glowstone", "1.12.2", "https://github.com/GlowstoneMC/Glowstone/releases/download/2021.7.0/glowstone.jar");

    private final String url;
    private final String title;
    private final String version;
    private final boolean proxy;

    GameServerVersion(final @NotNull String title, final @NotNull String version) {
        this(title, version, false);
    }

    GameServerVersion(final @NotNull String title, final @NotNull String version, final boolean proxy) {
        final var build = this.getBuildNumber(title, version);
        var paperVersion = version;
        if (paperVersion.equals("latest")) {
            paperVersion = this.getLatestVersion(title);
        }
        this.url =
            "https://papermc.io/api/v2/projects/" + title + "/versions/" + paperVersion + "/builds/" + build
                + "/downloads/" + title + "-" + paperVersion + "-" + build + ".jar";
        this.title = title;
        this.version = version;
        this.proxy = proxy;

        VERSIONS.put(getName(), this);
    }

    GameServerVersion(final @NotNull String title, final @NotNull String version, final @NotNull String url) {
        this.url = url;
        this.title = title;
        this.version = version;
        this.proxy = false;
        VERSIONS.put(getName(), this);
    }

    GameServerVersion(final @NotNull String title, final @NotNull String version, final boolean proxy, final @NotNull String url) {
        this.url = url;
        this.title = title;
        this.version = version;
        this.proxy = proxy;
        VERSIONS.put(getName(), this);
    }

    public static GameServerVersion getVersionByName(final @NotNull String value) {
        return VERSIONS.get(value);
    }

    public @NotNull String getName() {
        return this.title + (!this.version.equals("latest") ? "-" + this.version : "");
    }

    public boolean isProxy() {
        return this.proxy;
    }

    public String getJar() {
        return this.title + (!Objects.equals(this.version, "latest") ? "-" + this.version : "") + ".jar";
    }

    public int getBuildNumber(final @NotNull String title, final @NotNull String version) {
        var paperVersion = version;
        if (paperVersion.equals("latest")) {
            paperVersion = this.getLatestVersion(title);
        }
        final var document = this.paperApiRequest("https://papermc.io/api/v2/projects/" + title + "/versions/" + paperVersion + "/");
        if (document != null) {
            final List<Integer> buildNumbers = document.get("builds", TypeToken.getParameterized(List.class, Integer.class).getType());
            return buildNumbers.get(buildNumbers.size() - 1);
        } else {
            return -1;
        }
    }

    public @NotNull String getLatestVersion(final @NotNull String title) {
        final var document = this.paperApiRequest("https://papermc.io/api/v2/projects/" + title);
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
            final var inputStream = url.openStream();
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var document = new Document(inputStreamReader);
            inputStreamReader.close();
            inputStream.close();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

