package de.bytemc.cloud.api.exception.matcher;

public interface MatcherFactory<T> {

    Matcher build(T errorCode);

}
