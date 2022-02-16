package de.bytemc.cloud.api.logger;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public final class LoggerOutputStream extends ByteArrayOutputStream {

    private final LoggerProvider loggerProvider;
    private final LogType logType;

    public LoggerOutputStream(final LoggerProvider loggerProvider, final LogType logType) {
        this.loggerProvider = loggerProvider;
        this.logType = logType;
    }

    @Override
    public void flush() {
        final var input = this.toString(StandardCharsets.UTF_8);
        this.reset();
        if (input != null && !input.isEmpty()) {
            this.loggerProvider.logMessage(input, this.logType);
        }
    }

}
