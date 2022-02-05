package de.bytemc.cloud.api.events;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface IEventHandler {

    <T extends ICloudEvent> void registerEvent(@NotNull Class<T> clazz, @NotNull Consumer<T> event);

    <T extends ICloudEvent> void call(@NotNull T t);

}
