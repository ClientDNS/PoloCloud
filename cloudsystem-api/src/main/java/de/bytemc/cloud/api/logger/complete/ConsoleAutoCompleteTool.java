package de.bytemc.cloud.api.logger.complete;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;

public class ConsoleAutoCompleteTool implements Completer {

    private List<String> getInputConsoleSuggestions(String input) {
        //TODO
        return new ArrayList<>();
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        final String inputLine = parsedLine.line();

        final List<String> suggestions = new ArrayList<>(getInputConsoleSuggestions(inputLine));
        if (suggestions.isEmpty()) return;

        list.addAll(suggestions.stream().map(Candidate::new).toList());
    }
}
