package de.polocloud.base.exception;

public interface ErrorAction {

    void execute(Throwable throwable, ErrorHandler errorHandler);

}
