package de.bytemc.cloud.database;

import de.bytemc.network.promise.ICommunicationPromise;
import org.jetbrains.annotations.NotNull;

public interface IDatabaseManager {

    @NotNull IDatabase getDatabase();

    @NotNull ICommunicationPromise<Void> shutdown();

}
