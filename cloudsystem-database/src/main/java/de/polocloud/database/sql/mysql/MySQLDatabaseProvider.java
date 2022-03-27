package de.polocloud.database.sql.mysql;

import de.polocloud.database.DatabaseConfiguration;
import de.polocloud.database.sql.SQLDatabaseProvider;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class MySQLDatabaseProvider extends SQLDatabaseProvider {

    public MySQLDatabaseProvider(final DatabaseConfiguration config) throws SQLException {
        super(DriverManager.getConnection("jdbc:mysql://" + config.getHostname() + ":"
            + config.getPort() + "/" + config.getDatabase()
            + "?user=" + config.getUsername() + "&password=" + config.getPassword() + "&serverTimezone=UTC&autoReconnect=true"));
    }

}
