package de.polocloud.base.dependencies;

public enum Dependency {

    GSON(
        "com.google.code.gson",
        "gson",
        "2.9.0"
    ),
    JLINE(
        "org.jline",
        "jline",
        "3.21.0"),
    JANSI(
        "org.fusesource.jansi",
        "jansi",
        "2.4.0"
    ),
    COMMONS_IO(
        "commons-io",
        "commons-io",
        "2.11.0"
    ),
    NETTY_TRANSPORT(
        "io.netty",
        "netty-transport",
        "4.1.75.Final"),
    NETTY_COMMON(
        "io.netty",
        "netty-common",
        "4.1.75.Final"),
    NETTY_BUFFER(
        "io.netty",
        "netty-buffer",
        "4.1.75.Final"),
    NETTY_RESOLVER(
        "io.netty",
        "netty-resolver",
        "4.1.75.Final"),
    NETTY_TRANSPORT_EPOLL(
        "io.netty",
        "netty-transport-classes-epoll",
        "4.1.75.Final"),
    NETTY_UNIX_COMMON(
        "io.netty",
        "netty-transport-native-unix-common",
        "4.1.75.Final"),
    NETTY_CODEC(
        "io.netty",
        "netty-codec",
        "4.1.75.Final"),
    MYSQL_DRIVER(
        "mysql",
        "mysql-connector-java",
        "8.0.28"
    ),
    MONGO_DRIVER(
        "org.mongodb",
        "mongo-java-driver",
        "3.12.10"
    ),
    H2_DATABASE(
        "com.h2database",
        "h2",
        "2.1.210"
    );

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    private final String mavenRepoPath;
    private final String version;

    Dependency(final String groupId, final String artifactId, final String version) {
        this.mavenRepoPath = String.format(
            MAVEN_FORMAT,
            groupId.replace('.', '/'),
            artifactId,
            version,
            artifactId,
            version);
        this.version = version;
    }

    public String getMavenRepoPath() {
        return this.mavenRepoPath;
    }

    public String getVersion() {
        return this.version;
    }

    public String getFileName() {
        return this.name().toLowerCase().replace('_', '-') + "-" + this.version + ".jar";
    }

}
