package de.bytemc.cloud.api.logger;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.CloudAPITypes;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SimpleLoggerProvider extends Logger implements LoggerProvider {

    private final SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm:ss");

    //only for console
    @Getter
    private SimpleConsoleManager consoleManager;

    public SimpleLoggerProvider() {
        super("PoloCloud-Logger", null);

        this.setLevel(Level.ALL);
        this.setUseParentHandlers(false);

        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1\\$tT] [%4$-7s] %5\\$s %n");

        if (CloudAPI.getInstance().getCloudAPITypes().equals(CloudAPITypes.NODE)) {
            this.consoleManager = new SimpleConsoleManager(this);
        }
    }

    @Override
    public String getLog(@NotNull String text, @NotNull LogType logType) {
        String message = "§r" + text + "§r";
        if (logType != LogType.EMPTY) {
            message = " " + dataFormat.format(Calendar.getInstance().getTime()) + " §7" + (isWindows() ? "|" : "┃") + " §r" +
                logType.getTextField() + " " + (isWindows() ? ">" : "»") + " §r" + message + "§r";
        }
        return LoggerAnsiFactory.toColorCode(message);
    }

    @Override
    public void logMessage(@NotNull String text, @NotNull LogType logType) {
        final String coloredMessage = this.getLog(text, logType);
        if (CloudAPI.getInstance().getCloudAPITypes().equals(CloudAPITypes.NODE)) {
            final LineReader lineReader = this.consoleManager.getLineReader();
            lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
            lineReader.getTerminal().writer().println(coloredMessage);
            lineReader.getTerminal().flush();
            if (lineReader.isReading()) {
                lineReader.callWidget(LineReader.REDRAW_LINE);
                lineReader.callWidget(LineReader.REDISPLAY);
            }
        } else CloudAPI.getInstance().getCommandSender().sendMessage(text);
    }

    @Override
    public void logMessage(@NotNull String text) {
        this.logMessage(text, LogType.INFO);
    }

    @Override
    public void logMessages(final String... text) {
        for (final String s : text) this.logMessage(s);
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public void clearConsole() {
        if (this.isWindows()) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (IOException | InterruptedException exception) {
                exception.printStackTrace();
            }
            return;
        }
        System.out.println("\u001b[H\u001b[2J");
        System.out.flush();
    }

}
