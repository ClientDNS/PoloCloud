package de.bytemc.cloud.logger.exception;

import org.jetbrains.annotations.NotNull;

public interface IException {

    void onExecute(final @NotNull Exception exception);

}
