package de.polocloud.base.exception.matcher;

public interface MatcherFactory<T> {

    Matcher build(T errorCode);

}
