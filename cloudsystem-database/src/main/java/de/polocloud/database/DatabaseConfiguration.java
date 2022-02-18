package de.polocloud.database;

import lombok.Getter;

@Getter
public class DatabaseConfiguration {

    private String hostname = "localhost";
    private String database = "polocloud";
    private String username = "admin";
    private String password = "password";

    private int port = 3306;

    private DatabaseType databaseType = DatabaseType.MYSQL;

}
