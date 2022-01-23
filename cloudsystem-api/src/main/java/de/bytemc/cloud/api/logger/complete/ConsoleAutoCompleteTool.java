package de.bytemc.cloud.api.logger.complete;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleAutoCompleteTool implements Completer {

    private List<String> getInputConsoleSuggestions(String input){
        //TODO
        return new ArrayList<>();
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String inputLine = parsedLine.line();

        List<String> suggestions = new ArrayList<>(getInputConsoleSuggestions(inputLine));
        if(suggestions.isEmpty()) return;

        list.addAll(suggestions.stream().map(it -> new Candidate(it)).collect(Collectors.toList()));
    }
}
