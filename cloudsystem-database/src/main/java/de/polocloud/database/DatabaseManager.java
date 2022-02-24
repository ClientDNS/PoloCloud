package de.polocloud.database;

import org.jetbrains.annotations.NotNull;

public interface DatabaseManager {

    CloudDatabaseProvider getProvider();

    static DatabaseManager newInstance(@NotNull final DatabaseConfiguration configuration) {
        return new SimpleDatabaseManager(configuration);
    }

    void close();

}
