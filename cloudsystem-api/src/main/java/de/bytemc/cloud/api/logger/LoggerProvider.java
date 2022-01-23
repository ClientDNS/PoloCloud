package de.bytemc.cloud.api.logger;

public interface LoggerProvider {

    void logMessage(String text, LogType logType);

    void logMessage(String text);

    void logMessages(String... text);

    String getLog(String text, LogType logType);

    void clearConsole();

}
