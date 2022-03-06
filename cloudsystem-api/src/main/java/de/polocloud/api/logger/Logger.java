package de.polocloud.api.logger;

import org.jetbrains.annotations.NotNull;

public interface Logger {

    /**
     * logs a message
     * @param text the text to log
     * @param logType the type to log
     */
    void log(final @NotNull String text, final @NotNull LogType logType);

    /**
     * logs a message
     * @param text the text to log
     */
    default void log(final @NotNull String text) {
        this.log(text, LogType.INFO);
    }

    /**
     * logs a message
     * @param text the messages to log
     * @param logType the type to log
     */
    void log(final @NotNull String[] text, final @NotNull LogType logType);

    /**
     * logs messages
     * @param text the messages to log
     */
    void log(final @NotNull String... text);

    String format(final @NotNull String text, final @NotNull LogType logType);

}
