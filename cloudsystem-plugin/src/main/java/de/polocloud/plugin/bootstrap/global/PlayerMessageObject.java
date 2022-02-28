package de.polocloud.plugin.bootstrap.global;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerMessageObject {

    void sendMessage(@NotNull final String message);

    boolean hasPermission(@NotNull UUID uuid, @NotNull String permission);

}
