package de.polocloud.base.dependencies;

public enum Dependency {

    MYSQL_DRIVER(
        "mysql",
        "mysql-connector-java",
        "8.0.28"
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
