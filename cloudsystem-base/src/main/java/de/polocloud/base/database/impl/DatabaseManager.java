package de.polocloud.base.database.impl;

import de.polocloud.base.database.DatabaseConfiguration;
import de.polocloud.base.database.IDatabase;
import de.polocloud.base.database.IDatabaseManager;
import de.polocloud.base.database.impl.sql.DatabaseSqlImpl;
import de.polocloud.network.promise.ICommunicationPromise;
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
