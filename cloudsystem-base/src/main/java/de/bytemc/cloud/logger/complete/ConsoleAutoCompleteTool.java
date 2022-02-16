package de.bytemc.cloud.logger.complete;

import de.bytemc.cloud.Base;
import de.bytemc.cloud.api.command.CloudCommand;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class ConsoleAutoCompleteTool implements Completer {

    private List<String> getInputConsoleSuggestions(final String input) {
        if (input.isEmpty() || input.indexOf(' ') == -1) {
            final List<String> registeredCommands = new ArrayList<>();
            for (CloudCommand cachedCloudCommand : Base.getInstance().getCommandManager().getCachedCloudCommands()) {
                registeredCommands.add(cachedCloudCommand.getCommandName());
                registeredCommands.addAll(List.of(cachedCloudCommand.getAlias()));
            }
            final String[] splitInput = input.split(" ");
            final String toTest = splitInput[splitInput.length - 1];
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
