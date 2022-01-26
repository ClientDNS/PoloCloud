package de.bytemc.cloud.database;

import de.bytemc.network.promise.ICommunicationPromise;

public interface IDatabaseManager {

    IDatabase getDatabase();

    ICommunicationPromise<Void> shutdown();

}
