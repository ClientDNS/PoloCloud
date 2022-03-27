package de.polocloud.database;

import de.polocloud.database.mongo.MongoDatabaseProvider;
import de.polocloud.database.sql.h2.H2DatabaseProvider;
import de.polocloud.database.sql.mysql.MySQLDatabaseProvider;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class SimpleDatabaseManager implements DatabaseManager {

    private CloudDatabaseProvider provider;
    public static final String GROUP_TABLE = "cloudsystem_groups";

    public SimpleDatabaseManager(@NotNull final DatabaseConfiguration configuration) {
        try {
            switch (configuration.getDatabaseType()) {
                case MYSQL -> this.provider = new MySQLDatabaseProvider(configuration);
                case MONGODB -> this.provider = new MongoDatabaseProvider(configuration);
                case H2 -> this.provider = new H2DatabaseProvider();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() {
        this.provider.disconnect();
    }
}
