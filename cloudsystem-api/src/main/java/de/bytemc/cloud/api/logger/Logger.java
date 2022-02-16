package de.bytemc.cloud.api.logger;

import org.jetbrains.annotations.NotNull;

public interface Logger {

    /**
     * logs a message
     * @param text the text to log
     * @param logType the type to log
     */
    void logMessage(final @NotNull String text, final @NotNull LogType logType);

    /**
     * logs a message
     * @param text the text to log
     */
    default void logMessage(final @NotNull String text) {
        this.logMessage(text, LogType.INFO);
    }

    /**
     * logs messages
     * @param text the messages to log
     */
    void logMessages(final @NotNull String... text);

    String getLog(final @NotNull String text, final @NotNull LogType logType);

    /**
     * clears the console
     */
    void clearConsole();

}
