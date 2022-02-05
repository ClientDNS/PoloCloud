package de.bytemc.cloud.api.events;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface IEventHandler {

    /**
     * registers an event
     * @param clazz the class of the event
     * @param event the consumer to execute the event
     */
    <T extends ICloudEvent> void registerEvent(@NotNull Class<T> clazz, @NotNull Consumer<T> event);

    /**
     * calls an event
     * @param t the event to call
     */
    <T extends ICloudEvent> void call(@NotNull T t);

}
