package de.bytemc.cloud.command;

import de.bytemc.cloud.api.CloudAPI;
import de.bytemc.cloud.api.command.executor.ICommandSender;
import org.jetbrains.annotations.NotNull;

public final class DefaultCommandSender implements ICommandSender {

    @Override
    public void sendMessage(@NotNull String text) {
        CloudAPI.getInstance().getLoggerProvider().logMessage(text);
    }

}
