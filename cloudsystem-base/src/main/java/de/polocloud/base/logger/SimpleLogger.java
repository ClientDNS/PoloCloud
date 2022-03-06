package de.polocloud.base.logger;

import de.polocloud.api.logger.LogType;
import de.polocloud.api.logger.Logger;
import de.polocloud.api.logger.LoggerAnsiFactory;
import de.polocloud.base.console.SimpleConsoleManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.InfoCmp;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class SimpleLogger implements Logger {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Getter
    private final SimpleConsoleManager consoleManager;

    public SimpleLogger() {
        this.consoleManager = new SimpleConsoleManager(this);

        System.setOut(new PrintStream(new LoggerOutputStream(this, LogType.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(this, LogType.ERROR), true));
    }

    @Override
    public String format(@NotNull String text, @NotNull LogType logType) {
        var message = "§r" + text + "§r";
        if (logType != LogType.EMPTY) {
            message = " " + this.dateFormat.format(Calendar.getInstance().getTime()) + " §7" + (this.isWindows() ? "|" : "┃") + " §r" +
                logType.getTextField() + " " + (this.isWindows() ? ">" : "»") + " §r" + message + "§r";
        }
        return LoggerAnsiFactory.toColorCode(message);
    }

    @Override
    public void log(@NotNull String text, @NotNull LogType logType) {
        final var terminal = this.consoleManager.getTerminal();
        final var coloredMessage = this.format(text, logType);
        terminal.puts(InfoCmp.Capability.carriage_return);
        terminal.writer().println(coloredMessage);
        terminal.flush();
        this.consoleManager.redraw();
    }

    @Override
    public void log(final @NotNull String[] text, final @NotNull LogType logType) {
        final var terminal = this.consoleManager.getTerminal();
        terminal.puts(InfoCmp.Capability.carriage_return);
        for (final var s : text) {
            final var coloredMessage = this.format(s, logType);
            terminal.writer().println(coloredMessage);
        }
        terminal.flush();
        this.consoleManager.redraw();
    }

    @Override
    public void log(final @NotNull String... text) {
        this.log(text, LogType.INFO);
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

}
