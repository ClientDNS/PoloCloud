package de.polocloud.base.exception;

import de.polocloud.base.Base;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.logger.LogType;

import java.sql.SQLException;

public class DefaultExceptionCodes {

    public DefaultExceptionCodes() {
        ErrorHandler.defaultInstance()
            .onError(SQLException.class, ((throwable, errorHandler) -> {
                CloudAPI.getInstance().getLogger()
                    .log("§cSQLError occurred§7, check your §bDatabase credentials! §7(§b" + throwable.getMessage() + "§7)", LogType.ERROR);
                throwable.printStackTrace();
            }))
            .bindCode(1, errorCode -> throwable -> throwable instanceof RuntimeException, ((throwable, errorHandler) -> {
                CloudAPI.getInstance().getLogger()
                    .log("§cFatal exception occurred§7, cloud will try to stop... (§b" + throwable.getMessage() + "§7)", LogType.ERROR);
                throwable.printStackTrace();
                Base.getInstance().onShutdown();
            }));
    }

}
