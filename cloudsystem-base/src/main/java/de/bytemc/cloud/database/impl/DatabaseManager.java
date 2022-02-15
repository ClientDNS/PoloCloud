package de.bytemc.cloud.database.impl;

import de.bytemc.cloud.database.DatabaseConfiguration;
import de.bytemc.cloud.database.IDatabase;
import de.bytemc.cloud.database.IDatabaseManager;
import de.bytemc.cloud.database.impl.sql.DatabaseSqlImpl;
import de.bytemc.network.promise.ICommunicationPromise;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class DatabaseManager implements IDatabaseManager {

    @Getter
    private final IDatabase database;

    public DatabaseManager(final DatabaseConfiguration databaseConfiguration) {
        this.database = new DatabaseSqlImpl();
        this.database.connect(databaseConfiguration);
    }

    public @NotNull ICommunicationPromise<Void> shutdown(){
        return this.database.disconnect();
    }


}
