package de.bytemc.cloud.api.logger.exception;

import org.jetbrains.annotations.NotNull;

public interface IException {

    void onExecute(final @NotNull Exception exception);

}
