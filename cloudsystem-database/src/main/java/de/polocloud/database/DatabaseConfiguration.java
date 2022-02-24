package de.polocloud.database;

import lombok.Getter;

@Getter
public class DatabaseConfiguration {

    private final String hostname;
    private final String database;
    private final String username;
    private final String password;

    private final int port;

    private final DatabaseType databaseType;

    // default parameters
    public DatabaseConfiguration() {
        this.hostname = "127.0.0.1";
        this.database = "cloud";
        this.username = "admin";
        this.password = "password";
        this.port = 3306;
        this.databaseType = DatabaseType.MYSQL;
    }

}
