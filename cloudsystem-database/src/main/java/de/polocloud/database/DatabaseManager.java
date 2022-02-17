package de.polocloud.database;

import de.polocloud.database.manager.SQLCloudDatabaseHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class DatabaseManager implements IDatabaseManager {

    private ICloudDatabaseProvider provider;

    public DatabaseManager(@NotNull final DatabaseConfiguration configuration) {
        if(configuration.getDatabaseTypes() == DatabaseTypes.MYSQL) {
            SQLCloudDatabaseHandler provider = new SQLCloudDatabaseHandler(configuration);
            provider.connect();

            this.provider = provider;
        } else {
            //TODO MONGODB
        }
    }

    @Override
    public void close() {
        this.provider.disconnect();
    }
}
