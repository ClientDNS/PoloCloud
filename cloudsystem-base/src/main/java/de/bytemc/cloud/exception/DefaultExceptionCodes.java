package de.bytemc.cloud.exception;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.exception.ErrorHandler;
import de.bytemc.cloud.api.logger.LogType;

import java.sql.SQLException;

public class DefaultExceptionCodes {

    public DefaultExceptionCodes() {
        ErrorHandler.defaultInstance()
            .onError(SQLException.class, ((throwable, errorHandler) -> {
                if (CloudAPI.getInstance().getLoggerProvider() == null) {
                    System.err.println("SQLError occurred, check your database credentials! (" + throwable.getMessage() + ")");
                    return;
                }
                CloudAPI.getInstance().getLoggerProvider().logMessage("§cSQLError occurred§7, check your §bdatabase credentials! §7(§b" + throwable.getMessage() + "§7)", LogType.ERROR);
            }))
            .bindCode(1, errorCode -> throwable -> throwable instanceof RuntimeException, ((throwable, errorHandler) -> {
                if (CloudAPI.getInstance().getLoggerProvider() == null) {
                    System.err.println("Fatal exception occurred, stopping... (" + throwable.getMessage() + ")");
                    return;
                }
                CloudAPI.getInstance().getLoggerProvider().logMessage("§cFatal exception occurred§7, cloud will try to stop... (§b" + throwable.getMessage() + "§7)", LogType.ERROR);
                Base.getInstance().onShutdown();
            }));
    }
}
