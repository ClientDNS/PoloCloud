package de.bytemc.cloud.wrapper.logger;

import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.LoggerAnsiFactory;
import de.bytemc.cloud.api.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WrapperLoggerProvider implements Logger {

    private final SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void logMessage(@NotNull String text, @NotNull LogType logType) {
        System.out.println(text);
    }

    @Override
    public void logMessages(@NotNull String... text) {
        for (final String s : text) this.logMessage(s);
    }

    @Override
    public String getLog(@NotNull String text, @NotNull LogType logType) {
        String message = "§r" + text + "§r";
        if (logType != LogType.EMPTY) {
            message = " " + this.dataFormat.format(Calendar.getInstance().getTime()) + " §7" + (this.isWindows() ? "|" : "┃") + " §r" +
                logType.getTextField() + " " + (this.isWindows() ? ">" : "»") + " §r" + message + "§r";
        }
        return LoggerAnsiFactory.toColorCode(message);
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    @Override
    public void clearConsole() {}

}
