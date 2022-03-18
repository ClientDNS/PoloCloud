package de.polocloud.database;

import de.polocloud.database.manager.MongoCloudDatabseHandler;
import de.polocloud.database.manager.SQLCloudDatabaseHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class SimpleDatabaseManager implements DatabaseManager {

    private CloudDatabaseProvider provider;
    public static final String GROUP_TABLE = "cloudsystem_groups";

    public SimpleDatabaseManager(@NotNull final DatabaseConfiguration configuration) {
        if (configuration.getDatabaseType() == DatabaseType.MYSQL) {
            this.provider = new SQLCloudDatabaseHandler(configuration);
        } else if(configuration.getDatabaseType() == DatabaseType.MONGODB){
            this.provider = new MongoCloudDatabseHandler(configuration);
        }
    }


    @Override
    public void close() {
        this.provider.disconnect();
    }
}
