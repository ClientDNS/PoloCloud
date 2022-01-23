package de.bytemc.cloud.api.logger.exception;

public interface IException {

    void onExecute(Exception exception);
}
