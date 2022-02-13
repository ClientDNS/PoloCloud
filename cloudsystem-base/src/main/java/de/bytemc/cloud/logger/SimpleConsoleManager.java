package de.bytemc.cloud.logger;

import com.google.common.base.Throwables;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.LoggerProvider;
import de.bytemc.cloud.logger.builder.DefaultLineReader;
import de.bytemc.cloud.logger.runnable.ConsoleReadingThread;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;

@Getter
public final class SimpleConsoleManager {

    private final LoggerProvider loggerProvider;
    private Thread consoleReadingThread;
    private final LineReader lineReader;
    private final boolean windowsSystem;

    public SimpleConsoleManager(final LoggerProvider loggerProvider) {
        this.windowsSystem = ((SimpleLoggerProvider) loggerProvider).isWindows();
        this.lineReader = DefaultLineReader.read().preparedTerminal().addOptions().complete();
        this.loggerProvider = loggerProvider;

        this.loggerProvider.clearConsole();
    }

    public void start() {
        this.consoleReadingThread = new ConsoleReadingThread((SimpleLoggerProvider) loggerProvider, this.lineReader,
            s -> CloudAPI.getInstance().getCommandManager().execute(s), windowsSystem);
        this.consoleReadingThread.setUncaughtExceptionHandler((t, e) -> {
            CloudAPI.getInstance().getLoggerProvider().logMessage("An error...", LogType.ERROR);
            CloudAPI.getInstance().getLoggerProvider().logMessage("§7" + Throwables.getStackTraceAsString(e), LogType.ERROR);
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
