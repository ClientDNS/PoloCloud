package de.polocloud.base.console;

import de.polocloud.api.logger.LogType;
import de.polocloud.api.logger.Logger;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

import java.util.function.Consumer;

public final class ConsoleReadingThread extends Thread {

    private final String consolePrompt;

    private final LineReader lineReader;
    private final Consumer<String> line;

    public ConsoleReadingThread(final Logger logger, LineReader lineReader, Consumer<String> handle, Boolean windows) {
        this.lineReader = lineReader;
        this.line = handle;

        this.consolePrompt = logger.format("§bCloudsystem §7" + (windows ? ">" : "»") + " §f", LogType.EMPTY);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                this.line.accept(this.lineReader.readLine(this.consolePrompt));
            }
        } catch (UserInterruptException ignored) {}
    }

}
