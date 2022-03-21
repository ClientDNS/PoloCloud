package de.polocloud.base.console;

import de.polocloud.api.logger.Logger;
import de.polocloud.base.logger.SimpleLogger;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public final class SimpleConsoleManager {

    private final Logger logger;
    private Thread consoleReadingThread;
    private final Terminal terminal;
    private final LineReader lineReader;
    private final boolean windowsSystem;

    private final Queue<ConsoleInput> inputs;

    public SimpleConsoleManager(final Logger logger) throws IOException {
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
            .completer(new ConsoleCompleter(this))
            .build();
        this.logger = logger;
        this.clearConsole();

        this.inputs = new LinkedList<>();
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public LineReader getLineReader() {
        return this.lineReader;
    }

    public void start() {
        this.consoleReadingThread = new ConsoleReadingThread(this.logger, this, this.windowsSystem);
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

    public void shutdown() throws IOException {
        this.terminal.close();
    }

    public void shutdownReading() {
        this.consoleReadingThread.interrupt();
    }

    public void addInput(final Consumer<String> input, final List<String> tabCompletions) {
        this.inputs.add(new ConsoleInput(input, tabCompletions));
    }

    public Queue<ConsoleInput> getInputs() {
        return this.inputs;
    }

}
