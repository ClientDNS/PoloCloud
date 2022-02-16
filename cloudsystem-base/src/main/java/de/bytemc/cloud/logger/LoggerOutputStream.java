package de.bytemc.cloud.logger;

import de.bytemc.cloud.api.logger.LogType;
import de.bytemc.cloud.api.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public final class LoggerOutputStream extends ByteArrayOutputStream {

    private final Logger logger;
    private final LogType logType;

    public LoggerOutputStream(final Logger logger, final LogType logType) {
        this.logger = logger;
        this.logType = logType;
    }

    @Override
    public void flush() {
        final var input = this.toString(StandardCharsets.UTF_8);
        this.reset();
        if (input != null && !input.isEmpty()) {
            this.logger.logMessage(input, this.logType);
        }
    }

}
