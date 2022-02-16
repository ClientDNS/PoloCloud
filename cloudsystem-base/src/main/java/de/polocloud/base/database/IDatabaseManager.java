package de.polocloud.base.database;

import de.polocloud.network.promise.ICommunicationPromise;
import org.jetbrains.annotations.NotNull;

public interface IDatabaseManager {

    @NotNull IDatabase getDatabase();

    @NotNull ICommunicationPromise<Void> shutdown();

}
