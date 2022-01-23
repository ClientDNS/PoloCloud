package de.bytemc.cloud.api.logger.builder;

import com.google.common.base.Charsets;
import de.bytemc.cloud.api.logger.complete.ConsoleAutoCompleteTool;
import lombok.SneakyThrows;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

public class DefaultLineReader {

    private LineReaderBuilder lineReader;

    public static DefaultLineReader read(){
        return new DefaultLineReader();
    }
    public DefaultLineReader() {
        this.lineReader = LineReaderBuilder.builder();
    }

    public LineReader complete(){
        return this.lineReader.completer(new ConsoleAutoCompleteTool()).build();
    }

    @SneakyThrows
    public DefaultLineReader preparedTerminal(){
        this.lineReader.terminal(TerminalBuilder.builder().system(true).streams(System.in, System.out).encoding(Charsets.UTF_8).dumb(true).build());
        return this;
    }

    public DefaultLineReader addOptions(){
        this.lineReader.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true);
        this.lineReader.option(LineReader.Option.AUTO_REMOVE_SLASH, false);
        this.lineReader.option(LineReader.Option.INSERT_TAB, false);
        return this;
    }

}
