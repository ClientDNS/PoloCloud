package de.polocloud.api.event;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface EventHandler {

    /**
     * registers an event
     * @param clazz the class of the event
     * @param event the consumer to execute the event
     */
    <T extends CloudEvent> void registerEvent(@NotNull Class<T> clazz, @NotNull Consumer<T> event);

    /**
     * calls an event
     * @param t the event to call
     */
    <T extends CloudEvent> void call(@NotNull T t);

}
