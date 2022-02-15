package de.bytemc.cloud.api.exception;

public interface ErrorAction {

    void execute(Throwable throwable, ErrorHandler errorHandler);

}
