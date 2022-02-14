package de.bytemc.cloud.api.exception.matcher.impl;

import de.bytemc.cloud.api.exception.matcher.Matcher;

public record ErrorMatcher(Class<? extends Exception> errorClass) implements Matcher {

    @Override
    public boolean matches(Throwable throwable) {
        return errorClass.isInstance(throwable);
    }

}
