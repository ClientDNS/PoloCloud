package de.bytemc.cloud.database;

public interface IDatabaseManager {

    IDatabase getDatabase();

    void shutdown();

}
