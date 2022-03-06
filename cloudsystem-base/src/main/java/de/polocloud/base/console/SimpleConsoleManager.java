package de.polocloud.base.console;

import de.polocloud.api.logger.Logger;
import de.polocloud.base.logger.SimpleLogger;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.nio.charset.StandardCharsets;

@Getter
public final class SimpleConsoleManager {

    private final Logger logger;
    private Thread consoleReadingThread;
    private final Terminal terminal;
    private final LineReader lineReader;
    private final boolean windowsSystem;

    @SneakyThrows
    public SimpleConsoleManager(final Logger logger) {
        this.windowsSystem = ((SimpleLogger) logger).isWindows();
        this.terminal = TerminalBuilder.builder()
            .system(true)
            .streams(System.in, System.out)
            .encoding(StandardCharsets.UTF_8)
            .dumb(true)
            .build();
        this.lineReader = LineReaderBuilder.builder()
            .terminal(this.terminal)
            .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
            .option(LineReader.Option.AUTO_REMOVE_SLASH, false)
            .option(LineReader.Option.INSERT_TAB, false)
            .completer(new ConsoleCompleter())
            .build();
        this.logger = logger;
        this.clearConsole();
    }

    public void start() {
        this.consoleReadingThread = new ConsoleReadingThread(this.logger, this.lineReader, this.windowsSystem);
        this.consoleReadingThread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
        this.consoleReadingThread.start();
    }

    public void clearConsole() {
        this.terminal.puts(InfoCmp.Capability.clear_screen);
        this.terminal.flush();
        this.redraw();
    }

    public void redraw() {
        if (this.lineReader.isReading()) {
            this.lineReader.callWidget(LineReader.REDRAW_LINE);
            this.lineReader.callWidget(LineReader.REDISPLAY);
        }
    }

    @SneakyThrows
    public void shutdown() {
        this.lineReader.getTerminal().close();
    }

    public void shutdownReading() {
        this.consoleReadingThread.interrupt();
    }

}
