package de.bytemc.cloud.database.impl;

import de.bytemc.cloud.database.IDatabase;
import de.bytemc.cloud.database.IDatabaseManager;
import de.bytemc.cloud.database.impl.sql.DatabaseSqlImpl;
import lombok.Getter;

public class DatabaseManager implements IDatabaseManager {

    @Getter
    private IDatabase database;

    public DatabaseManager() {
        this.database = new DatabaseSqlImpl();
        this.database.connect();
    }

    public void shutdown(){
        this.database.disconnect();
    }


}
