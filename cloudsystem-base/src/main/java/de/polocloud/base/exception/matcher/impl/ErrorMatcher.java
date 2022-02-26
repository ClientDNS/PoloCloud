package de.polocloud.base.exception.matcher.impl;

import de.polocloud.base.exception.matcher.Matcher;

public record ErrorMatcher(Class<? extends Exception> errorClass) implements Matcher {

    @Override
    public boolean matches(Throwable throwable) {
        return this.errorClass.isInstance(throwable);
    }

}
