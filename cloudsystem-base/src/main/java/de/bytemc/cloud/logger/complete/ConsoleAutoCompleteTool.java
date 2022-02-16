package de.bytemc.cloud.logger.complete;

import com.google.common.collect.Lists;
import de.bytemc.cloud.Base;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class ConsoleAutoCompleteTool implements Completer {

    private List<String> getInputConsoleSuggestions(final String input) {
        if (input.isEmpty() || input.indexOf(' ') == -1) {
            final Collection<String> registeredCommands = Base.getInstance().getCommandManager().getCachedCloudCommands().keySet();
            final var splitInput = input.split(" ");
            final var toTest = splitInput[splitInput.length - 1];
            final List<String> result = new LinkedList<>();
            for (final String s : registeredCommands) {
                if (s != null && (toTest.trim().isEmpty() || s.toLowerCase().contains(toTest.toLowerCase()))) {
                    result.add(s);
                }
            }

            if (result.isEmpty() && !registeredCommands.isEmpty()) {
                result.addAll(registeredCommands);
            }

            return result;
        } else {
            final var arguments = Lists.newArrayList(input.split(" "));
            final var command = Base.getInstance().getCommandManager().getCachedCloudCommands().get(arguments.get(0));
            if (command != null) {
                arguments.remove(0);
                return command.tabComplete(arguments.toArray(new String[]{}));
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        final String inputLine = parsedLine.line();

        final List<String> suggestions = new ArrayList<>(this.getInputConsoleSuggestions(inputLine));
        if (suggestions.isEmpty()) return;

        list.addAll(suggestions.stream().map(Candidate::new).toList());
    }

}
