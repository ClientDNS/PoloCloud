package de.polocloud.base.console;

import de.polocloud.api.logger.LogType;
import de.polocloud.api.logger.Logger;
import de.polocloud.base.Base;
import org.jline.reader.LineReader;

public final class ConsoleReadingThread extends Thread {

    private final String consolePrompt;

    private final LineReader lineReader;

    public ConsoleReadingThread(final Logger logger, LineReader lineReader, boolean windows) {
        super("PoloCloud-Console-Thread");
        this.lineReader = lineReader;
        this.consolePrompt = logger.format("§bCloudsystem §7" + (windows ? ">" : "»") + " §f", LogType.EMPTY);
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            final var line = this.lineReader.readLine(this.consolePrompt);
            if (line != null && !line.isEmpty()) {
                Base.getInstance().getCommandManager().execute(line);
            }
        }
    }

}
