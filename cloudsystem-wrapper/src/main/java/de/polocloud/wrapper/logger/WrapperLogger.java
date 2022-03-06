package de.polocloud.wrapper.logger;

import de.polocloud.api.logger.LogType;
import de.polocloud.api.logger.LoggerAnsiFactory;
import de.polocloud.api.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WrapperLogger implements Logger {

    private final SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void log(@NotNull String text, @NotNull LogType logType) {
        System.out.println(text);
    }

    @Override
    public void log(@NotNull String[] text, @NotNull LogType logType) {
        for (final var s : text) this.log(s, logType);
    }

    @Override
    public void log(@NotNull String... text) {
        for (final var s : text) this.log(s);
    }

    @Override
    public String format(@NotNull String text, @NotNull LogType logType) {
        var message = "§r" + text + "§r";
        if (logType != LogType.EMPTY) {
            message = " " + this.dataFormat.format(Calendar.getInstance().getTime()) + " §7" + (this.isWindows() ? "|" : "┃") + " §r" +
                logType.getTextField() + " " + (this.isWindows() ? ">" : "»") + " §r" + message + "§r";
        }
        return LoggerAnsiFactory.toColorCode(message);
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

}
