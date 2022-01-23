package de.bytemc.cloud.api.command.executor;

public interface ICommandSender {

    void sendMessage(String text);

    ExecutorType getCommandType();

}
