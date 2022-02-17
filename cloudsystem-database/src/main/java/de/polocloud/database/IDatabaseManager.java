package de.polocloud.database;

import org.jetbrains.annotations.NotNull;

public interface IDatabaseManager {

    ICloudDatabaseProvider getProvider();

    static IDatabaseManager newInstance(@NotNull final DatabaseConfiguration configuration) {
        return new DatabaseManager(configuration);
    }

    void close();

}
