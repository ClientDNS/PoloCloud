package de.bytemc.cloud.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatabaseConfiguration {

    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

}
