package de.polocloud.base.logger;

import de.polocloud.api.logger.LogType;
import de.polocloud.api.logger.LoggerAnsiFactory;
import de.polocloud.api.logger.Logger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class SimpleLogger implements Logger {

    private final SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm:ss");

    @Getter
    private final SimpleConsoleManager consoleManager;

    public SimpleLogger() {
        this.consoleManager = new SimpleConsoleManager(this);

        System.setOut(new PrintStream(new LoggerOutputStream(this, LogType.INFO)));
        System.setErr(new PrintStream(new LoggerOutputStream(this, LogType.ERROR)));
    }

    @Override
    public String format(@NotNull String text, @NotNull LogType logType) {
        String message = "§r" + text + "§r";
        if (logType != LogType.EMPTY) {
            message = " " + this.dataFormat.format(Calendar.getInstance().getTime()) + " §7" + (this.isWindows() ? "|" : "┃") + " §r" +
                logType.getTextField() + " " + (this.isWindows() ? ">" : "»") + " §r" + message + "§r";
        }
        return LoggerAnsiFactory.toColorCode(message);
    }

    @Override
    public void log(@NotNull String text, @NotNull LogType logType) {
        final String coloredMessage = this.format(text, logType);
        final LineReader lineReader = this.consoleManager.getLineReader();
        lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        lineReader.getTerminal().writer().println(coloredMessage);
        lineReader.getTerminal().flush();
        if (lineReader.isReading()) {
            lineReader.callWidget(LineReader.REDRAW_LINE);
            lineReader.callWidget(LineReader.REDISPLAY);
        }
    }

    @Override
    public void log(final String... text) {
        for (final String s : text) this.log(s);
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
