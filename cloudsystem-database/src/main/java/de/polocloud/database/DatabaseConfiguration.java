package de.polocloud.database;

import lombok.Getter;

@Getter
public class DatabaseConfiguration {

    private final String hostname = "localhost";
    private final String database = "polocloud";
    private final String username = "admin";
    private final String password = "password";

    private final int port = 3306;

    private final DatabaseType databaseType = DatabaseType.MYSQL;

}
