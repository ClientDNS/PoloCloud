package de.polocloud.base.exception;

import de.polocloud.base.exception.matcher.Matcher;

public record ActionEntry(Matcher matcher,
                          ErrorAction action) {

}

