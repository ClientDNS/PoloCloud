package de.bytemc.cloud.api.command.executor;

import org.jetbrains.annotations.NotNull;

public interface ICommandSender {

    /**
     * sends a message
     * @param text the text to send
     */
    void sendMessage(@NotNull String text);

    /**
     * @return the command type
     */
    @NotNull ExecutorType getCommandType();

}
