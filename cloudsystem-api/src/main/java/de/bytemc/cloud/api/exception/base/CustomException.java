package de.bytemc.cloud.api.exception.base;

import de.bytemc.cloud.api.exception.ErrorHandler;

public class CustomException extends Exception {

    public CustomException(Runnable runnable) {
        ErrorHandler.defaultInstance().onError(this.getClass(), (throwable, errorHandler) -> runnable.run());
    }
}
