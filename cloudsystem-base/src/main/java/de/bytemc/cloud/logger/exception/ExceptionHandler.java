package de.bytemc.cloud.logger.exception;

import com.google.common.base.Throwables;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import de.bytemc.cloud.api.logger.LogType;

public final class ExceptionHandler {

    public ExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (CloudAPI.getInstance() == null) {
                e.printStackTrace();
                return;
            }

            if (CloudAPI.getInstance().getCloudAPITypes() == CloudAPITypes.NODE) {
                if (CloudAPI.getInstance().getLoggerProvider() == null) {
                    e.printStackTrace();
                    return;
                }

                CloudAPI.getInstance().getLoggerProvider().logMessage("An error...", LogType.ERROR);
                CloudAPI.getInstance().getLoggerProvider().logMessage("§7" + Throwables.getStackTraceAsString(e), LogType.ERROR);
            }
        });
    }

}
