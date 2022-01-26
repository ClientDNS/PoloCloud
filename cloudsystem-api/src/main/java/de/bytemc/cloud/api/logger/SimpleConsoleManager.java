package de.bytemc.cloud.api.logger;

import com.google.common.base.Throwables;
import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.logger.builder.DefaultLineReader;
import de.bytemc.cloud.api.logger.runnable.ConsoleReadingThread;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;

@Getter
public class SimpleConsoleManager {

    private final LoggerProvider loggerProvider;
    private Thread consoleReadingThread;
    private LineReader lineReader;
    private boolean windowsSystem;

    public SimpleConsoleManager(LoggerProvider loggerProvider) {
        this.windowsSystem = ((SimpleLoggerProvider) loggerProvider).isWindows();
        this.lineReader = DefaultLineReader.read().preparedTerminal().addOptions().complete();
        this.loggerProvider = loggerProvider;

        loggerProvider.clearConsole();
    }

    public void start() {

        this.consoleReadingThread = new ConsoleReadingThread((SimpleLoggerProvider) loggerProvider, this.lineReader, s -> CloudAPI.getInstance().getCommandManager().execute(s), windowsSystem);
        this.consoleReadingThread.setUncaughtExceptionHandler((t, e) -> {
            CloudAPI.getInstance().getLoggerProvider().logMessage("An error...", LogType.ERROR);
            CloudAPI.getInstance().getLoggerProvider().logMessage("§7" + Throwables.getStackTraceAsString(e), LogType.ERROR);
        });
        this.consoleReadingThread.start();
    }

    @SneakyThrows
    public void shutdown() {
        this.lineReader.getTerminal().close();
    }

    public void shutdownReading(){
        this.consoleReadingThread.interrupt();
    }

    public void handleInput(String input){

    }

}
