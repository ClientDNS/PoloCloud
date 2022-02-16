package de.polocloud.base.exception.custom;

import de.polocloud.base.exception.ErrorHandler;

public class CustomException extends Exception {

    public CustomException(Runnable runnable) {
        ErrorHandler.defaultInstance().onError(this.getClass(), (throwable, errorHandler) -> runnable.run());
    }
}
