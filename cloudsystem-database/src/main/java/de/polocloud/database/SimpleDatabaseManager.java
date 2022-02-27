package de.polocloud.database;

import de.polocloud.database.manager.SQLCloudDatabaseHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class SimpleDatabaseManager implements DatabaseManager {

    private CloudDatabaseProvider provider;

    public SimpleDatabaseManager(@NotNull final DatabaseConfiguration configuration) {
        if (configuration.getDatabaseType() == DatabaseType.MYSQL) {
            var provider = new SQLCloudDatabaseHandler(configuration);
            provider.connect();

            this.provider = provider;
        }
    }

    @Override
    public void close() {
        this.provider.disconnect();
    }
}
