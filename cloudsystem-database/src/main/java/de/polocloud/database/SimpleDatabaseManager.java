package de.polocloud.database;

import de.polocloud.database.mongo.MongoDatabaseProvider;
import de.polocloud.database.sql.SQLDatabaseProvider;
import de.polocloud.database.sql.mysql.MySQLDatabaseProvider;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class SimpleDatabaseManager implements DatabaseManager {

    private CloudDatabaseProvider provider;
    public static final String GROUP_TABLE = "cloudsystem_groups";

    public SimpleDatabaseManager(@NotNull final DatabaseConfiguration configuration) {
        if (configuration.getDatabaseType() == DatabaseType.MYSQL) {
            this.provider = new MySQLDatabaseProvider(configuration);
        } else if(configuration.getDatabaseType() == DatabaseType.MONGODB){
            this.provider = new MongoDatabaseProvider(configuration);
        }
    }


    @Override
    public void close() {
        this.provider.disconnect();
    }
}
