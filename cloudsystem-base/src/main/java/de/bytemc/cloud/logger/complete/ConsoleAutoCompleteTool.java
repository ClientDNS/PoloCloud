package de.bytemc.cloud.logger.complete;

import de.bytemc.cloud.Base;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.*;

public final class ConsoleAutoCompleteTool implements Completer {

    private List<String> getInputConsoleSuggestions(final String input) {
        var arguments = input.split(" ");
        if (input.isEmpty() || input.indexOf(' ') == -1) {
            final Collection<String> registeredCommands = Base.getInstance().getCommandManager().getCachedCloudCommands().keySet();
            final var toTest = arguments[arguments.length - 1];
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
            final var command = Base.getInstance().getCommandManager().getCachedCloudCommands().get(arguments[0]);
            if (arguments.length > 1) {
                if (input.endsWith(" ")) {
                    arguments = Arrays.copyOfRange(arguments, 1, arguments.length + 1);
                    arguments[arguments.length - 1] = "";
                } else {
                    arguments = Arrays.copyOfRange(arguments, 1, arguments.length);
                }
            }
            if (command != null) {
                return command.tabComplete(arguments);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        final String inputLine = parsedLine.line();

        final List<String> suggestions = this.getInputConsoleSuggestions(inputLine);
        if (suggestions.isEmpty()) return;

        suggestions.stream().map(Candidate::new).forEach(list::add);
    }

}
