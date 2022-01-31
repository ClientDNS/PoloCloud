package de.bytemc.cloud.api.events;

import java.util.function.Consumer;

public interface IEventHandler {

    <T extends ICloudEvent> void registerEvent(Class<T> clazz, Consumer<T> event);

    <T extends ICloudEvent> void call(T t);
}
