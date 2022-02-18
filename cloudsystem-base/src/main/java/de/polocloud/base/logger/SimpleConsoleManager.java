package de.polocloud.base.logger;

import de.polocloud.base.Base;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.logger.LogType;
import de.polocloud.api.logger.Logger;
import de.polocloud.base.console.ConsoleCompleter;
import de.polocloud.base.console.ConsoleReadingThread;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import java.nio.charset.StandardCharsets;

@Getter
public final class SimpleConsoleManager {

    private final Logger logger;
    private Thread consoleReadingThread;
    private final LineReader lineReader;
    private final boolean windowsSystem;

    @SneakyThrows
    public SimpleConsoleManager(final Logger logger) {
        this.windowsSystem = ((SimpleLogger) logger).isWindows();
        this.lineReader = LineReaderBuilder.builder()
            .terminal(TerminalBuilder.builder().system(true).streams(System.in, System.out).encoding(StandardCharsets.UTF_8).dumb(true).build())
            .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
            .option(LineReader.Option.AUTO_REMOVE_SLASH, false)
            .option(LineReader.Option.INSERT_TAB, false)
            .completer(new ConsoleCompleter())
            .build();
        this.logger = logger;

        this.logger.clearConsole();
    }

    public void start() {
        this.consoleReadingThread = new ConsoleReadingThread(this.logger, this.lineReader,
            s -> Base.getInstance().getCommandManager().execute(s), this.windowsSystem);
        this.consoleReadingThread.setUncaughtExceptionHandler((t, e) -> {
            CloudAPI.getInstance().getLogger().log("An error...", LogType.ERROR);
            e.printStackTrace();
        });
        this.consoleReadingThread.start();
    }

    @SneakyThrows
    public void shutdown() {
        this.lineReader.getTerminal().close();
    }

    public void shutdownReading() {
        this.consoleReadingThread.interrupt();
    }

}
