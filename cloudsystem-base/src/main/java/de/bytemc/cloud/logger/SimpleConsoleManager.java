package de.bytemc.cloud.logger;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.Logger;
import de.bytemc.cloud.logger.complete.ConsoleAutoCompleteTool;
import de.bytemc.cloud.logger.runnable.ConsoleReadingThread;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

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
            .terminal(TerminalBuilder.builder().system(true).streams(System.in, System.out).encoding(Charsets.UTF_8).dumb(true).build())
            .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
            .option(LineReader.Option.AUTO_REMOVE_SLASH, false)
            .option(LineReader.Option.INSERT_TAB, false)
            .completer(new ConsoleAutoCompleteTool())
            .build();
        this.logger = logger;

        this.logger.clearConsole();
    }

    public void start() {
        this.consoleReadingThread = new ConsoleReadingThread(this.logger, this.lineReader,
            s -> Base.getInstance().getCommandManager().execute(s), this.windowsSystem);
        this.consoleReadingThread.setUncaughtExceptionHandler((t, e) -> {
            CloudAPI.getInstance().getLogger().logMessage("An error...", LogType.ERROR);
            CloudAPI.getInstance().getLogger().logMessage("§7" + Throwables.getStackTraceAsString(e), LogType.ERROR);
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
