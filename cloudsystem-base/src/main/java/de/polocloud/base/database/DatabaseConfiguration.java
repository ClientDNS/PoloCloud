package de.polocloud.base.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("ClassCanBeRecord")
@Getter
@AllArgsConstructor
public class DatabaseConfiguration {

    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

}
