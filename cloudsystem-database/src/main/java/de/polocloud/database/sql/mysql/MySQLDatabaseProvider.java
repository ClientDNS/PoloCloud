package de.polocloud.database.sql.mysql;

import de.polocloud.database.DatabaseConfiguration;
import de.polocloud.database.sql.SQLDatabaseProvider;

public final class MySQLDatabaseProvider extends SQLDatabaseProvider {

    public MySQLDatabaseProvider(final DatabaseConfiguration config) {
        super(config, "jdbc:mysql");
    }

}
